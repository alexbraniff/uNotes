using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace RESTfulNotes
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services

            // Web API config.Routes
            config.MapHttpAttributeRoutes();

            //config.Routes.MapHttpRoute(
            //    name: "DefaultApi",
            //    routeTemplate: "api/{controller}/{id}",
            //    defaults: new { id = RouteParameter.Optional }
            //);

            config.Routes.MapHttpRoute(
                name: "Register",
                routeTemplate: "rest/register",
                defaults: new { controller = "Rest", action = "Register" }
            );

            config.Routes.MapHttpRoute(
                name: "Login",
                routeTemplate: "rest/login",
                defaults: new { controller = "Rest", action = "Login" }
            );

            config.Routes.MapHttpRoute(
                name: "Logout",
                routeTemplate: "rest/logout",
                defaults: new { controller = "Rest", action = "Logout" }
            );

            config.Routes.MapHttpRoute(
                name: "Ping",
                routeTemplate: "rest/ping",
                defaults: new { controller = "Rest", action = "Ping" }
            );

            config.Routes.MapHttpRoute(
                name: "Create",
                routeTemplate: "rest/create/{table id}",
                defaults: new { controller = "Rest", action = "Create" }
            );

            config.Routes.MapHttpRoute(
                name: "Read",
                routeTemplate: "rest/read/{table_id}",
                defaults: new { controller = "Rest", action = "Read" }
            );

            config.Routes.MapHttpRoute(
                name: "Read",
                routeTemplate: "rest/read/{table_id}/{record_id}",
                defaults: new { controller = "Rest", action = "Read" }
            );

            config.Routes.MapHttpRoute(
                name: "Update",
                routeTemplate: "rest/update/{table_id}/{record_id}",
                defaults: new { controller = "Rest", action = "Update" }
            );

            config.Routes.MapHttpRoute(
                name: "Delete",
                routeTemplate: "rest/delete/{table_id}/{record_id}",
                defaults: new { controller = "Rest", action = "Delete" }
            );

            config.Routes.MapHttpRoute(
                name: "Toggle",
                routeTemplate: "rest/toggle/{table_id}/{record_id}",
                defaults: new { controller = "Rest", action = "Toggle" }
            );

            config.Routes.MapHttpRoute(
                name: "Permission",
                routeTemplate: "rest/permission/{user_id}",
                defaults: new { controller = "Rest", action = "Permission" }
            );

            config.Routes.MapHttpRoute(
                name: "Permission",
                routeTemplate: "rest/permission/{user_id}/{table_id}",
                defaults: new { controller = "Rest", action = "Permission" }
            );

            config.Routes.MapHttpRoute(
                name: "Permission",
                routeTemplate: "rest/permission/{user_id}/{table_id}/{record_id}",
                defaults: new { controller = "Rest", action = "Permission" }
            );
        }
    }
}
