var margin = {top: 10, right: 30, bottom: 30, left: 60};
var width = 460 - margin.left - margin.right;
var height = 400 - margin.top - margin.bottom;
var riskScoresByAge = [];//this is an array of objects {x:birthYear, y:score}, there should be 30, once it is full, we will empty it and start over to avoid duplication, so there is a running 1 minute and then it starts over
var riskScoresByCustomer = {};//this is a map of customer emails to their risk profile, once it is full (30 keys), we will empty it, otherwise we would have duplicates

var scatterChartData = {
    datasets: [{
        label: 'Spark Result Set',
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgb(255,255,255)',
        data:riskScoresByAge,
    }]
};


var webSocket  = new WebSocket("ws://"+location.hostname+":"+location.port+"/socket");
    webSocket.onmessage = event => {
        var customerRisk = JSON.parse(event.data);

        if (riskScoresByAge.length >=30){
            riskScoresByAge = [];
            riskScoresByCustomer = {};
        }

        var graphCoordinate = {x: customerRisk.birthYear, y: customerRisk.score};
        riskScoresByAge.push(graphCoordinate);//this array will be used to display the graph
        scatterChartData.datasets[0].data=riskScoresByAge;
        window.myScatter.update();
    }

    webSocket.onclose =  () => {
        alert("Thank you for using STEDI, your graph session has ended");
        window.location.href = "/timer.html";
    };

var ctx = document.getElementById('risk-over-time').getContext('2d');

var color = Chart.helpers.color;

window.onload = () => {
    window.myScatter = Chart.Scatter(ctx, {
        data:scatterChartData,
        options: {
            title: {
                display: true,
                text: 'STEDI Population Risk by Birth Year'
            },
        }
    });
};

