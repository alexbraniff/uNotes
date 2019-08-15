using System;

namespace RESTfulNotes.Models.Requests
{
    public class RegisterRequest
    {
        public string UserAlias;
        public string PasswordHash;
        public string Salt;
        public string AppSessionUUID;
    }
}