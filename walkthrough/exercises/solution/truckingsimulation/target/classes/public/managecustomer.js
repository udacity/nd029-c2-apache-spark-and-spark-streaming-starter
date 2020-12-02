

var customerName = "";
var phone = "";
var bday = "";
var form = "";
var elements = "";

function setcustomername(){
    customerName = $("#cn").val();
}

function setemail(){
    email = $("#email").val();
}

function setphone(){
    phone = $("#phone").val();
}

function setbday(){
    bday = $("#bday").val();
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

function findcustomer(){
    var headers = { "suresteps.session.token": localStorage.getItem("token")};
    $.ajax({
        type: 'GET',
        url: `/customer/${email}`,
        contentType: 'application/text',
        dataType: 'text',
        headers: headers,
        success: function(data) {
            localStorage.setItem("customer",data);
            window.location.href="/timer.html";
        }
    });
}

function createcustomer(){
    var customer = {
        customerName : customerName,
        email : email,
        phone : phone,
        birthDay: bday
    }

    var headers = { "suresteps.session.token": localStorage.getItem("token")};
//    $.ajax({
//        type: 'POST',
//        url: '/createcustomer',
//        data: '{"customerName":"'+ customerName +'","email":"' + email +
//                '", "phone":"' + phone + '", "birthDay":"' + bday + '"}',
//        headers: {
//            'suresteps.session.token' : localStorage.getItem("token")
//        })
    $.ajax({
        type: 'POST',
        url: '/customer',
        data: JSON.stringify(customer),
        contentType: 'application/text',
        dataType: 'text',
        headers: headers,
        success: function(data) {
            localStorage.setItem("customer",JSON.stringify(customer));
            window.location.href=data
        }
    });
}

