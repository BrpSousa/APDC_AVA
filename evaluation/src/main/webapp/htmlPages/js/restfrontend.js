window.onload = function(){
	var frms = $('form{name="login]');
	frms[0].onsubmit = captureData;
}


captureData = function(event) {
    var data = $('form[name="login"]').jsonify();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "https://apdc-evaluation.appspot.com/rest/login/v2",
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        dataType: "json"
,
        success: function(response) {
        	if(response) {
                alert("Got token with id: " + response.tokenID);
                // Store token id for later use in localStorage
                localStorage.setItem('tokenID'
        , response.tokenID);
            }
            else {
                alert("No response");
            }
        },
        error: function(response) {
        	alert("Error: "+ response.status);
        },
        data: JSON.stringify(data)
    });
    event.preventDefault();
};

