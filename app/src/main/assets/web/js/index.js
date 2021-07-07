
var flag = false;

function changeBackgroundColor(){
    var body = document.getElementsByTagName("body")[0];
        
    flag = !flag;
    
    if(flag){
        body.style.backgroundColor = "black";
        body.style.color = "white";
    }
    else{
        body.style.backgroundColor = "antiquewhite";
        body.style.color = "black";    
    }
}

