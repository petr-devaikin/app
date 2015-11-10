var canvas, ctx;
//Marker size and coordinates
var values = {};
values["h"] = 210, 
values["w"] = 295, 
values["s"] = 32, 
values["oneX"] = 47,
values["oneY"] = 28,
values["twoX"] = 222,
values["twoY"] = 28,
values["threeX"] = 47,
values["threeY"] = 148,
values["fourX"] = 221,
values["fourY"] = 148;

var bodyPanel = $("#bodyPanel");	

function init() {
	canvas = document.getElementById('can');
	ctx = canvas.getContext("2d");
	var one = $("#topleft");
	var two = $("#topright");
	var three = $("#bottomleft");
	var four = $("#bottomright");
	setPanelSize(bodyPanel);
	setMarkerPositions(one, two, three, four, bodyPanel);
	ctx.canvas.width  = bodyPanel.width;
	ctx.canvas.height = bodyPanel.height;
	
}

function setPanelSize(bodyPanel){
	var ratio = getHeightWidthRatio(values["h"], values["w"]);
	var wHeight = window.innerHeight - 10;
	bodyPanel.height = wHeight;
	bodyPanel.css("height",wHeight+"px");
	bodyPanel.width = wHeight * ratio;
	bodyPanel.css("width",wHeight * ratio+"px");
}

function setMarkerPositions(one, two, three, four, bodyPanel){
	var markerSize = getMarkerSize(values["w"], values["s"]);
			
	one.css("width", bodyPanel.width/markerSize + "px");
	
	two.css("width", bodyPanel.width/markerSize + "px");
	
	three.css("width", bodyPanel.width/markerSize + "px");
	
	four.css("width", bodyPanel.width/markerSize + "px");
	
	var coordinates = getMarkerPositions(values, bodyPanel);
	
	one.css("top",coordinates["oneY"]);
	one.css("left",coordinates["oneX"]);
	
	two.css("top",coordinates["twoY"]);
	two.css("left",coordinates["twoX"]);
	
	three.css("top",coordinates["threeY"]);
	three.css("left",coordinates["threeX"]);
	
	four.css("top",coordinates["fourY"]);
	four.css("left",coordinates["fourX"]);
}

function getHeightWidthRatio(height, width){
return width/height;
}

function getMarkerSize(width, size){
	return width/size;
}

function getHWRatio(values, bodyPanel){
	var wRatio = values["w"]/bodyPanel.width;
	var hRatio = values["h"]/bodyPanel.height;
	
	var ratio = {};
	ratio["w"] = wRatio;
	ratio["h"] = hRatio;
	
	return ratio;
}

function getMarkerPositions(values, bodyPanel){
	var ratio = getHWRatio(values, bodyPanel);
	var wRatio = ratio["w"];
	var hRatio = ratio["h"];
	
	var coordinates = {};
	
	coordinates["oneX"] = values["oneX"]/wRatio;
	coordinates["oneY"] = values["oneY"]/hRatio;
	
	coordinates["twoX"] = values["twoX"]/wRatio;
	coordinates["twoY"] = values["twoY"]/hRatio;
	
	coordinates["threeX"] = values["threeX"]/wRatio;
	coordinates["threeY"] = values["threeY"]/hRatio;
	
	coordinates["fourX"] = values["fourX"]/wRatio;
	coordinates["fourY"] = values["fourY"]/hRatio;
	
	return coordinates;
}

function convertCMtoPXCoordinates(x, y){
	var coordinates = {};
	var ratio = getHWRatio(values, bodyPanel);
	coordinates["x"] = x/ratio["w"];
	coordinates["y"] = y/ratio["h"];
	
	return coordinates;
}

$(document).ready(function(){
	var URL = window.location.hostname;
	
	setInterval(function(){ 
		$.ajax({
		Method:"GET",
		url: "http://"+URL+":3000/gesture",
		success: function(result){
			if(result != "idle"){
				var obj = jQuery.parseJSON(result);
				var coordiantes = convertCMtoPXCoordinates(obj.x,obj.y);
				ctx.fillRect(coordiantes["x"],coordiantes["y"],3,3);
			}
		},
		error: function(){
			var c = document.getElementById('can');
			var ctx = c.getContext("2d");
			ctx.font = "30px Arial";
			ctx.strokeText("Error",10,50);
			ctx.strokeText("404", 20, 30);
		}
		});
	}, 100);
});