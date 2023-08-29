	var rowNum=0;
	function addRowTable_Booking(tableId){
		var table = document.getElementById(tableId);
		var row = document.createElement("TR");
		
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		var cell1 = row.insertCell(0);

		var element1 = document.createElement("input");
		element1.type = "checkbox";
		element1.name = "select";
		element1.id = "select"+rowNum;
		cell1.appendChild(element1);
		cell1.style.width="10%" ;

		var td1 = document.createElement("TD")
		var id1="\"dtlBookDate"+rowNum+"\"";
		var strHtml1 = "<input id='"+id1+"'  name='"+id1+"' type='text' style='width:100%' data-inputmask=\"'alias': 'dd/mm/yyyy'\" class='form-control' maxlength='10'></input>";
		td1.innerHTML = strHtml1.replace(/!rowNum!/g,rowNum);
		td1.style.width='6%';

		var td1 = document.createElement("TD")
		var id1="\"dtlBranch"+rowNum+"\"";
		
		var strHtml1 = "<input id='"+id1+"'  name='"+id1+"' type='text' style='width:100%' data-inputmask=\"'alias': 'dd/mm/yyyy'\" class='form-control' maxlength='10'></input>";
		td1.innerHTML = strHtml1.replace(/!rowNum!/g,rowNum);
		td1.style.width='8%';
		
		row.appendChild(td1);
		rowNum = parseInt(rowNum) + 1;
	}
