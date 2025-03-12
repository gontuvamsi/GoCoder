function Logout() {
    $.ajax({
        url: "http://localhost:7050/GoCoder/session/logout",
        type: "POST",
        success: function(response) {
         
            window.location.href = "login.html";
        },
        error: function(xhr, status, error) {
            console.error("Logout failed: " + error);
            alert("Logout failed! Please try again.");
        }
    });
}
