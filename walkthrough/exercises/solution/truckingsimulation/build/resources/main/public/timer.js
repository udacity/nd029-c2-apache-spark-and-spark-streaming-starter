    var clicks = 0;
    var stopwatch;
    var runningstate = 0; // 1 means the timecounter is running 0 means counter stopped
    var stoptime = 0;
    var currenttime;
    var usertoken=localStorage.getItem("token");
    var stepsTaken = [];
    var starttime;
    var previousStepTime;
    var customer = JSON.parse(localStorage.getItem("customer"));
    var startandstopbutton;
    var counterbutton;
    var timerUIWebSocket = new WebSocket(`ws://${location.hostname}:${location.port}/timeruiwebsocket`)

    $(document).ready(function(){
        $('#dob').html(customer.birthDay);
        startandstopbutton = document.getElementById('startandstopbutton');
        counterbutton = document.getElementById('counterbutton');

    });
//    var webSocket  = new WebSocket("ws://"+location.hostname+":"+location.port+"/socket");
//    webSocket.onopen = function (event){webSocket.send("StartReading~"+usertoken)};
//    webSocket.onmessage = function (webSocketPayload) {updateStepCount(webSocketPayload);};
//    webSocket.onclose = function () {
//        alert("Thank you for visiting Sure Steps, your session has ended");
//        window.location.href = "/index.html";
//    };

    document.onkeyup = (e) => {
        if (e.which == 89) {
            onStep();
        }
    };

    var setupUISocket = () => {
        timerUIWebSocket.onclose = () =>{
            alert("The workspace or server has shut down this connection. You will be re-directed to the login page.")
            window.location.href="/";
        }
    }

    var fetchToggleState = () => {

        var headers = { "suresteps.session.token": localStorage.getItem("token")};
        $.ajax({
            type: 'GET',
            url: '/simulation',
            contentType: 'application/text',
            dataType: 'text',
            headers: headers,
            statusCode: {
                200: (simulationActive) => {
                    var active = JSON.parse(simulationActive);

                    if (active === true){
                        $('#enablesimulationcheckbox').prop('checked', true);
                    } else{
                        $('#enablesimulationcheckbox').prop('checked', false);
                    }
                },
                401: () => window.location.href="/",
            },
        });

    }

    // this function is called when the toggle is activated/de-activated to enable simulated user traffic
    var simulationUpdate = (object) => {
        if ($(object).is(":checked")){
          $.ajax({
              type: 'POST',
              url: '/simulation',
              statusCode:{
                    401: () => window.location.href="/",
              },
              headers: { "suresteps.session.token": localStorage.getItem("token")},
              contentType: "application/text",
              dataType: 'text'
          });
        } else{
          $.ajax({
              type: 'DELETE',
              url: '/simulation',
              statusCode:{
                401: () => window.location.href="/",
              },
              headers: { "suresteps.session.token": localStorage.getItem("token")},
              contentType: "application/text",
              dataType: 'text'
          });        }
    }

    var saveRapidStepTest = (rapidStepTest) => {
        $.ajax({
            type: 'POST',
            url: '/rapidsteptest',
            data: JSON.stringify(rapidStepTest), // or JSON.stringify ({name: 'jonas'}),
            statusCode:{
                401: () => window.location.href="/",
            },
            headers: { "suresteps.session.token": localStorage.getItem("token")},
            contentType: "application/text",
            dataType: 'text'
        });

    }

    var getRiskScore = () => {
        $.ajax({
            type: 'GET',
            url: '/riskscore/'+customer.email,
            success: function(data) {
                var customerRisk = JSON.parse(data);
                document.getElementById('score').innerHTML = customerRisk.score;
            },
            headers: { "suresteps.session.token": localStorage.getItem("token")},
            contentType: "application/text",
            dataType: 'text'
        });

    }


    var updateStepCount = (webSocketPayload) => {
        if(webSocketPayload.data=="startTimer"){
            startandstop();
        } else if (webSocketPayload.data.indexOf("stepCount")>-1 && runningstate ==1){
            onStep();
        }
    }

    function onStep() {
        var stepDate = new Date();
        var stepTime = stepDate.getTime();
        if (previousStepTime==null){
            previousStepTime=starttime;
        }
        var timeTakenForStep = stepTime-previousStepTime;
        stepsTaken.push(timeTakenForStep);
        previousStepTime = stepTime;
        clicks += 1;
        document.getElementById("clicks").innerHTML = clicks;
        if(clicks==30){
        	startandstop();
        	var testTime = stepTime-starttime;
            var rapidStepTest = {
               token: localStorage.getItem("token"),
               startTime: starttime,
               stopTime: stepTime,
               testTime: testTime,
               totalSteps: 30,
               stepPoints: stepsTaken,
               customer: customer
            };
            saveRapidStepTest(rapidStepTest);
            getRiskScore();
            clicks=0;
            previousStepTime=null;
            stepsTaken = [];
 //           webSocket.close();
        }

	 };

    var timecounter = (starttime) => {
        currentdate = new Date();
                stopwatch = document.getElementById('stopwatch');
         
        var timediff = currentdate.getTime() - starttime;
        if(runningstate == 0)
            {
            timediff = timediff + stoptime
            }
        if(runningstate == 1)
            {
            stopwatch.value = formattedtime(timediff);
            refresh = setTimeout('timecounter(' + starttime + ');',10);
            }
        else
            {
            window.clearTimeout(refresh);
            stoptime = timediff;
            }
    }
 

    function startandstop() {
      if(runningstate==0)
      {
        startdate = new Date();
        starttime = startdate.getTime();
        startandstop.value = 'Stop';
        startandstop.disabled=true;
        counterbutton.disabled=false;
        runningstate = 1;
        timecounter(starttime);
      }
      else
      {
        pageStateStopped();
      }
    }

    var pageStateStopped = () => {
        startandstop.value = 'Start';
        startandstop.disabled=false;
        counterbutton.disabled=true;
        runningstate = 0;
    }

    function resetstopwatch() {
        stoptime = 0;
        window.clearTimeout(refresh);
      
        if(runningstate == 1)
        {
            var resetdate = new Date();
            var resettime = resetdate.getTime();
            timecounter(resettime);
        }
        else
        {
            stopwatch.value = "0:0:0";
            document.getElementById("clicks").innerHTML = 0;
            document.getElementById('score').innerHTML='';
        }
    }

    var formattedtime = (unformattedtime) => {
        var decisec = Math.floor(unformattedtime/100) + '';
        var second = Math.floor(unformattedtime/1000);
        var minute = Math.floor(unformattedtime/60000);
        decisec = decisec.charAt(decisec.length - 1);
        second = second - 60 * minute + '';
        return minute + ':' + second + ':' + decisec;
    }

    fetchToggleState();

    setupUISocket();
