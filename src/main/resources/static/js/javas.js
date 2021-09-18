	//検索ボタンを作成
	document.getElementById("button").onclick = function() {
		const textbox = document.getElementById("message")
		const value = textbox.value;
		console.log(value);
		displayTable(value);
	};

	//Enterキーを押すと検索ボタンと同じ動きをする
	let ent = document.querySelector(".enter");
	ent.onkeydown = function(e){
		if(e.keyCode == 13){
			document.getElementById("button").click();
		};
	}

	//テーブル作成
	let displayTable = function(value){
		//table要素を取得
		var tableElem = document.getElementById('table');
		//行要素を取得
		var rowElems = tableElem.rows;
		//取得した行(tr要素)を繰り返しHTMLを取得
		var htmlstr = '';
		for(i=0,rlen = rowElems.length; i<rlen; i++){
			//列要素を取得
		    var cellsElems = rowElems[i].cells;
			var flag = 0;
			for(j = 0,clen = cellsElems.length; j<clen; j++){
				if(cellsElems[j].getAttribute("search") == "yes"){
					if(cellsElems[j].textContent.indexOf(value) < 0){
						if(flag == 0){
							rowElems[i].style.display = "none";
							rowElems[i].classList.remove("result");
						};
					} else {
						flag = 1;
						rowElems[i].style.display = "table-row";
						rowElems[i].classList.add("result");
					}
				}else{
					console.log("no search");
				}

			}
		}
		// 件数を更新する
		let result = document.getElementById("result");
		let r = document.querySelectorAll(".result");
		result.innerText = "合計："+r.length+"件";
	};