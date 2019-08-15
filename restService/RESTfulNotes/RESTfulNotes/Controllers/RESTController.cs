using Npgsql;
using RESTfulNotes.Models.Requests;
using RESTfulNotes.Models.Results;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Results;
using System.Web.Mvc;
using System.Web.Script.Serialization;
using System.Reflection;

namespace RESTfulNotes.Controllers
{
    public class RESTController : ApiController
    {
        bool debug = true;
        string connStr = String.Format("Server={0};Port={1};User Id={2};Password={3};Database={4};", (string)ConfigurationManager.AppSettings["Server"], ConfigurationManager.AppSettings["port"], ConfigurationManager.AppSettings["UserID"], (string)ConfigurationManager.AppSettings["Password"], ConfigurationManager.AppSettings["Database"]);

        private class AllowJsonGetAttribute : ActionFilterAttribute
        {
            public override void OnResultExecuting(ResultExecutingContext filterContext)
            {
                var jsonResult = filterContext.Result as JsonResult;

                if (jsonResult == null)
                {
                    throw new ArgumentException("Action does not return a JsonResult, attribute AllowJsonGet is not allowed");
                }

                jsonResult.JsonRequestBehavior = JsonRequestBehavior.AllowGet;

                base.OnResultExecuting(filterContext);
            }
        }

        private int GetSessionExpirationIntervalHours()
        {
            return 8;
        }

        private string GetCommandName(MethodBase method)
        {
            string[] methodQualifiers = method.ToString().Split('.');
            int commandIndex = methodQualifiers.Length - 1;
            return methodQualifiers[commandIndex];
        }

        private bool HasPermissions(MethodBase method, int userID)
        {
            return true;
        }

        /// Register REST Command
        /// <summary>
        /// HTTP POST Request
        /// Must receive x-www-form-urlencoded data with single Key 
        /// "SerializedRequest" = Serialized Json Object containing 
        /// DB-unique User Name and a valid Password Hash, Salt, and
        /// App Session UUID, all as strings
        /// 
        /// Validates App Session and inserts User, Session, and
        /// User_session records for the new User
        /// </summary>
        /// <returns>
        /// IHttpActionResult:
        /// 
        /// BadRequestErrorMessageResult
        ///     if POST data or App Session UUID is invalid, 
        ///     or if User Alias is not DB-unique
        /// InternalServerErrorResult
        ///     if SQL Command fails
        /// JsonResult<RegisterResult>
        ///     if registration is successful
        /// </returns>
        [System.Web.Http.HttpPost]
        [System.Web.Http.HttpGet]
        public IHttpActionResult Register(HttpRequestMessage SerializedRequest)
        {
            string requestData = Uri.UnescapeDataString((string)SerializedRequest.Content.ReadAsStringAsync().Result);
            string sub;

            try
            {
                sub = requestData.Substring(0, 18);
            }
            catch (Exception x)
            {
                return new BadRequestErrorMessageResult("Error: POST data was not of mime type \"application/x-www-form-urlencoded\" with single key \"SerializedRequest\" as a Serialized Json Object containing DB-unique User Name \"UserAlias\" and a valid Password Hash \"PasswordHash\", Salt \"Salt\", and App Session UUID \"AppSessionUUID\", all as strings", this);
            }

            if (requestData == null || requestData == string.Empty || sub != "SerializedRequest=")
            {
                return new BadRequestErrorMessageResult("Error: POST data was not of mime type \"application/x-www-form-urlencoded\" with single key \"SerializedRequest\" as a Serialized Json Object containing DB-unique User Name \"UserAlias\" and a valid Password Hash \"PasswordHash\", Salt \"Salt\", and App Session UUID \"AppSessionUUID\", all as strings", this);
            }

            string requestStr = requestData.Split('=')[1];

            RegisterRequest request;

            try
            {
                request = new JavaScriptSerializer().Deserialize<RegisterRequest>(requestStr);
            }
            catch (Exception x)
            {
                return new BadRequestErrorMessageResult("Error: POST data was not of mime type \"application/x-www-form-urlencoded\" with single key \"SerializedRequest\" as a Serialized Json Object containing DB-unique User Name \"UserAlias\" and a valid Password Hash \"PasswordHash\", Salt \"Salt\", and App Session UUID \"AppSessionUUID\", all as strings", this);
            }

            Guid AppSession;

            if (!Guid.TryParse(request.AppSessionUUID, out AppSession))
            {
                return new BadRequestErrorMessageResult("Error: AppSessionUUID is invalid", this);
            }
            else
            {
                DataSet ds = new DataSet();
                DataTable dt = new DataTable();
                int UserID = -1;
                int SessionID = -1;
                int UserSessionID = -1;
                Guid Token = Guid.NewGuid();

                using (NpgsqlConnection conn = new NpgsqlConnection(connStr))
                {
                    conn.Open();

                    using (NpgsqlCommand cmd = new NpgsqlCommand(String.Format("SELECT COUNT(*) FROM \"{0}\".\"user\" WHERE \"user\".\"alias\" = '{1}'", ConfigurationManager.AppSettings["Schema"], request.UserAlias), conn))
                    {
                        object count;

                        try
                        {
                            count = cmd.ExecuteScalar();
                        }
                        catch (Exception x)
                        {
                            return new InternalServerErrorResult(this);
                        }

                        if ((Int64)count > 0)
                        {
                            return new BadRequestErrorMessageResult("Error: User Name is taken", this);
                        }
                    }

                    // Begin a transaction
                    NpgsqlTransaction tran;

                    try
                    {
                        tran = conn.BeginTransaction();
                    }
                    catch (Exception x)
                    {
                        return new InternalServerErrorResult(this);
                    }

                    // Insert User record and get UserID
                    using (var cmd = new NpgsqlCommand())
                    {
                        cmd.Connection = conn;
                        cmd.Transaction = tran;
                        cmd.CommandText = String.Format("INSERT INTO \"{0}\".\"user\" (\"active\", \"alias\", \"password_hash\", \"salt\") VALUES (@active, @alias, @password_hash, @salt) RETURNING \"user\".\"id\"", ConfigurationManager.AppSettings["Schema"]);
                        cmd.Parameters.AddWithValue("active", true);
                        cmd.Parameters.AddWithValue("alias", request.UserAlias);
                        cmd.Parameters.AddWithValue("password_hash", request.PasswordHash);
                        cmd.Parameters.AddWithValue("salt", request.Salt);

                        try
                        {
                            object user_id = cmd.ExecuteScalar();

                            if (user_id == null)
                            {
                                tran.Rollback();
                                conn.Close();
                                return new InternalServerErrorResult(this);
                            }
                            else
                            {
                                UserID = (int)user_id;
                            }
                        }
                        catch (Exception x)
                        {
                            tran.Rollback();
                            conn.Close();
                            return new InternalServerErrorResult(this);
                        }
                    }

                    //Insert Session record and SessionID
                    using (var cmd = new NpgsqlCommand())
                    {
                        cmd.Connection = conn;
                        cmd.Transaction = tran;
                        cmd.CommandText = String.Format("INSERT INTO \"{0}\".\"session\" (\"token\", \"app_session\", \"expires\") VALUES (@token, @app_session, @expires) RETURNING \"session\".\"id\"", ConfigurationManager.AppSettings["Schema"]);
                        cmd.Parameters.AddWithValue("token", Token);
                        cmd.Parameters.AddWithValue("app_session", request.AppSessionUUID);
                        cmd.Parameters.AddWithValue("expires", DateTime.Now.AddHours(GetSessionExpirationIntervalHours()));

                        try
                        {
                            object session_id = cmd.ExecuteScalar();

                            if (session_id == null)
                            {
                                tran.Rollback();
                                conn.Close();
                                return new InternalServerErrorResult(this);
                            }
                            else
                            {
                                SessionID = (int)session_id;
                            }
                        }
                        catch (Exception x)
                        {
                            tran.Rollback();
                            conn.Close();
                            return new InternalServerErrorResult(this);
                        }
                    }

                    // Insert User_session record
                    using (var cmd = new NpgsqlCommand())
                    {
                        cmd.Connection = conn;
                        cmd.Transaction = tran;
                        cmd.CommandText = String.Format("INSERT INTO \"{0}\".\"user_session\" (\"active\", \"user\", \"session\") VALUES (@active, @user, @session) RETURNING \"user_session\".\"id\"", ConfigurationManager.AppSettings["Schema"]);
                        cmd.Parameters.AddWithValue("active", true);
                        cmd.Parameters.AddWithValue("user", UserID);
                        cmd.Parameters.AddWithValue("session", SessionID);

                        try
                        {
                            object user_session_id = cmd.ExecuteScalar();

                            if (user_session_id == null)
                            {
                                tran.Rollback();
                                conn.Close();
                                return new InternalServerErrorResult(this);
                            }
                            else
                            {
                                UserSessionID = (int)user_session_id;
                            }
                        }
                        catch (Exception x)
                        {
                            tran.Rollback();
                            conn.Close();
                            return new InternalServerErrorResult(this);
                        }
                    }

                    tran.Commit();
                    conn.Close();
                }

                // Make a new JSONResult with the User's ID and Token UUID
                RegisterResult result = new RegisterResult();
                result.UserID = UserID;
                result.Token = Token.ToString();
                JsonResult<RegisterResult> response = Json(result);
                return response;
            }
        }

        public IHttpActionResult Login()
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Logout()
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Ping()
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Create(UInt64 table_id)
        {
            int UserID = -1;
            try
            {
                if (!HasPermissions(MethodBase.GetCurrentMethod(), UserID))
                {
                    return new BadRequestErrorMessageResult(
                        String.Format(
                            "Error: User with ID={0} does not have permission " +
                            "to execute REST Command {1} on table with ID={2}", 
                            Convert.ToString(UserID), 
                            GetCommandName(MethodBase.GetCurrentMethod()), 
                            Convert.ToString(table_id)
                        ), 
                        this
                    );
                }
            }
            catch (Exception x)
            {
                return new BadRequestErrorMessageResult(
                    String.Format(
                        "Error: User ID = {0}, Message = {1}", 
                        Convert.ToString(UserID), 
                        x.Message
                    ), 
                    this
                );
            }
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Read(UInt64 table_id)
        {
            int UserID = -1;
            try
            {
                if (!HasPermissions(MethodBase.GetCurrentMethod(), UserID))
                {
                    return new BadRequestErrorMessageResult(
                        String.Format(
                            "Error: User with ID={0} does not have permission " +
                            "to execute REST Command {1} on table with ID={2}", 
                            Convert.ToString(UserID), 
                            GetCommandName(MethodBase.GetCurrentMethod()), 
                            Convert.ToString(table_id)
                        ), 
                        this
                    );
                }
            }
            catch (Exception x)
            {
                return new BadRequestErrorMessageResult(
                    String.Format(
                        "Error: User ID = {0}, Message = {1}", 
                        Convert.ToString(UserID), 
                        x.Message
                    ), 
                    this
                );
            }
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Read(UInt64 table_id, UInt64 record_id)
        {
            int UserID = -1;
            try
            {
                if (!HasPermissions(MethodBase.GetCurrentMethod(), UserID))
                {
                    return new BadRequestErrorMessageResult(
                        String.Format(
                            "Error: User with ID={0} does not have permission " +
                            "to execute REST Command {1} on table with ID={2} for record with ID={3}",
                            Convert.ToString(UserID),
                            GetCommandName(MethodBase.GetCurrentMethod()),
                            Convert.ToString(table_id),
                            Convert.ToString(record_id)
                        ),
                        this
                    );
                }
            }
            catch (Exception x)
            {
                return new BadRequestErrorMessageResult(
                    String.Format(
                        "Error: User ID = {0}, Message = {1}",
                        Convert.ToString(UserID),
                        x.Message
                    ),
                    this
                );
            }
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Update(UInt64 table_id, UInt64 record_id)
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Delete(UInt64 table_id, UInt64 record_id)
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Toggle(UInt64 table_id, UInt64 record_id)
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Permission(UInt64 user_id)
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Permission(UInt64 user_id, UInt64 table_id)
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }

        public IHttpActionResult Permission(UInt64 user_id, UInt64 table_id, UInt64 record_id)
        {
            // This is a dummy result
            NotFoundResult response = new NotFoundResult(this);
            return response;
        }
    }
}