var userName = "";
var password = "";
var passwordRegEx=/((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!]).{6,40})/;

function setusername(){
    userName = $("#un").val();
}

function setuserpassword(){
    password = $("#pw").val();
    var valid=passwordRegEx.exec(password);
    if (!valid){
        console.log('invalid password');
    }
}

function setvpwd(){
    vpwd = $("#vpwd").val();
    var valid=passwordRegEx.exec(vpwd);
    if (!valid){
        console.log('invalid password');
    }
}

function savetoken(token){
// whatever passes as token should save into local storage
    if (window.localStorage){
     localStorage.setItem("token", token);
    }

}

function checkexpiredtoken(){
// read token from local storage - check with ajax call
    if(window.localStorage){
    usertoken = localStorage.getItem("token");
    $.ajax({
       type: 'GET',
        url: '/checkToken',
        data: JSON.stringify({usertoken}),
        success: function(data){savetoken(data)},
        contentType: "application/text",
        dataType: 'text' })
    }
}

function userlogin(){
    setuserpassword();
    setusername();
    $.ajax({
        type: 'POST',
        url: '/login',
        data: JSON.stringify({userName, password}),
        success: function(data) {savetoken(data);
                    window.location.href = "/timer.html"
            },
        contentType: "application/text",
        dataType: 'text'
    });

}

function readonlyforms(formid){
    form = document.getElementById(formid);
    elements = form.elements;
    for (i = 0, len = elements.length; i < len; ++i) {
    elements[i].readOnly = true;
    }
    createbutton();
}
 function pwsDisableInput( element, condition ) {
        if ( condition == true ) {
            element.disabled = true;

        } else {
            element.removeAttribute("disabled");
        }

 }

function createbutton(){
    var button = document.createElement("input");
    button.type = "button";
    button.value = "OK";
    button.onclick = window.location.href = "/index.html";
    context.appendChild(button);
}


function createuser(){
    $.ajax({
        type: 'POST',
        url: '/user',
        data: JSON.stringify({userName, 'email': userName, password, 'verifyPassword': vpwd, 'accountType':'Personal'}),//we are using the email as the user name
        success: function(data) { alert(data);
//        readonlyforms("newUser");
//        alert(readonlyforms("newUser"));
        window.location.href = "/index.html"},
        contentType: "application/text",
        dataType: 'text'
    });
}

function getstephistory(){
      $.ajax({
            type: 'POST',
            url: '/stephistory',
            data: JSON.stringify({userName}),
            success: function(data) { alert(data);
            json = $.parseJSON(data);
            $('#results').html(json.name+' Total Steps: ' + json.stepTotal)},
            contentType: "application/text",
            dataType: 'text'
        });
}