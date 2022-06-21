

function sendPositionPost(position) {
	var form = document.createElement("form");

	form.setAttribute("charset", "UTF-8");
	form.setAttribute("method", "Post");
	form.setAttribute("action", "/champions");
	
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "position");
	hiddenField.setAttribute("value", position);
	form.appendChild(hiddenField);
	
	document.body.appendChild(form);

	form.submit();
}

function sendChampPost(champion) {
	var form = document.createElement("form");

	form.setAttribute("charset", "UTF-8");
	form.setAttribute("method", "Post");
	form.setAttribute("action", "/champions/detail");
	
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "champion");
	hiddenField.setAttribute("value", champion);
	form.appendChild(hiddenField);
	
	document.body.appendChild(form);

	form.submit();
}