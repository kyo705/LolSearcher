
function findChampName(champid, num){
	var champJsonData = fetch("./json/champion.json")
	.then(response => {
		return response.json();
	})
	.then(jsondata => {
		let k = document.querySelectorAll(".curParticipant_row")[num];
		
		var img = document.createElement("IMG");
		img.setAttribute("width", "50");
	    img.setAttribute("height", "50");

		var champList = jsondata.data;
		for (let i in champList){
			if(champList[i].key === champid){
				img.setAttribute("src", "/champion/"+ champList[i].id + ".png");
				break;
			}
		}
		
		k.prepend(img);
	});		
}

function findspellName(spellid, num){
	var champJsonData = fetch("./json/summoner.json")
	.then(response => {
		return response.json();
	})
	.then(jsondata => {
		var img = document.createElement("IMG");
		img.setAttribute("width", "25");
	    img.setAttribute("height", "25");

		var spellList = jsondata.data;
		for (let i in spellList){
			if(spellList[i].key === spellid){
				img.setAttribute("src", "/spell/"+ spellList[i].id + ".png");
				break;
			}
		}
		
		var div = document.querySelectorAll(".spell_column")[num];
		div.appendChild(img);
	});		
}

function timeFormat(startTimeStamp){
	var time = parseInt((new Date().getTime()-startTimeStamp)/(1000))
	const target = document.getElementById("duringTime");
	
	var h = 0;
	var s = 0;
	if(time>=60){
		h = parseInt(time/60);
		s = time-h*60;
	}else{
		s = time;
	}
	
	if(h<10){
		h = "0" + h;
	}
	
	target.textContent = h +" : "+s;
}
