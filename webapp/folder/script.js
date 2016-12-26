var ul,name,ws,ch,iwin,g,m;
var abort = false;
var jj={},h={};
var list=[];
var lists=[];
var l=[];
var li=[];
var z= [];
function createXHR(){
    try{
        st = new XMLHttpRequest();
    }catch(e){
        try{
            st = new ActiveXObject("Msxml2.XMLHTTP.6.0");
        }catch(ee){
            st = new ActiveXObject("Msxml2.XMLHTTP");
        }
    }
    return st;
}
var s = createXHR();
function isTimeOut(){
    if(s.readyState!==4){
        abort = true;
        s.abort();
        alert('Request Timeout');
    }
}
function check(){
    var ss = document.getElementById("sname");
    var sup = document.getElementById('sup');
    var u = ss.value;
    var o = document.getElementById("serror");
    if(u===''){
        o.innerHTML = "Username can't be empty";
        o.className = "serror";
        ss.focus();
        sup.disabled=true;
        return;
    }else if(sup.disabled){sup.disabled = false;}
    s.open("get","/Trending/welcome?u="+u);
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            if(s.responseText.trim()!==""){
                o.className = "serror";
                sup.disabled=true;
            }
            else{
                if(sup.disabled){sup.disabled = false;}
                o.className= "hide";
            }
            o.innerHTML = s.responseText;
        }
    };
}
function login(){
    var u = document.getElementById("lname").value;
    var p = document.getElementById("lpword").value;
    var d = document.getElementById("login");
    if(document.getElementById('lname').validity.valueMissing){
        d.innerHTML = "Username can't be empty";
        d.className = 'serror';
        document.getElementById("lname").focus();
        return;
    }
    s.open("post","/Trending/welcome/l");
    window.setTimeout(isTimeOut,10000);
    s.setRequestHeader("content-type","application/x-www-form-urlencoded");
    s.send('u='+u+'&p='+p);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            d.innerHTML = s.responseText;
             if(d.innerHTML!==''){
                 document.getElementById('lname').value='';
             }
        }
    };
}
function vChange(e){
    if(e.target.value!==""){
    m = e.target.files;
    var ifr = document.getElementById('ifr');
    var c=0;
    var stt='';
    var st = '';
    for(i=0;i<m.length;i++){
        c = c + m[i].size;
            g = m[i];z[i]=g;
            var dd = URL.createObjectURL(g);
            if(g.type.charAt(0)==='i'){    
                stt += '<div><img width="300px" height="300px" src="'+dd+'"/><input placeholder="Add a Caption" id="tf'+i+'"/></div>';  
            }else if(g.type.charAt(0)==='a'){
                 stt += '<div><audio controls src="'+dd+'"></audio><input placeholder="Add a Caption" id="tf'+i+'"></div>'; 
            }else{
                 stt += '<div><video controls src="'+dd+'"/></video><input placeholder="Add a Caption" id="tf'+i+'"></div>'; 
            }
    }
    if(c>=10*1024*1024){
        e.target.value = "";
    }else{
        st += stt+'<button onclick="upload(event)" id="abb">Upload</button>';
        ifr.innerHTML = st;ifr.className='ifr';
    }
}}
function upload(e){ 
    var tfarr=[];
    for(i=0;i<z.length;i++){
        var tf = document.getElementById('tf'+i).value;
        if(tf==='')tf='@'+Math.random();
        tfarr[i]=tf;
    }z.length=0;
    jj['tf'] = tfarr;
    jj['z'] = m;
    document.getElementById('ifr').innerHTML ='';
    document.getElementById('ifr').className='hide';
}
function createorjoin(e){
    var t = document.getElementById("disc").value;
    var desc = document.getElementById("desc").value;
    var mv = document.getElementById("media").value;
    if(e.type==="click"){
        var fd = new FormData();
        fd.append('%',t);
        fd.append('&',desc);
        e.target.innerHTML = "Creating Topic...";
        e.target.disabled='disabled';
        if(mv!==""&&t!==""){
            var tf = jj['tf'];
            var z = jj['z'];
            for(i=0;i<tf.length;i++){
                fd.append(tf[i],z[i]);
            }
            s.open("post","/Trending/welcome/z");
            s.send(fd);
        }else if(t!==""){
            s.open("post","/Trending/welcome/q");
            s.send(fd);
        }
        document.getElementById('load').className = 'load';
        setTimeout(isTimeOut,120000);
    }
    else if(e.type==="keypress"&&e.key==="Enter"){
        if(t!==""){
            s.open("post","/Trending/welcome/disc");
            s.setRequestHeader("content-type","application/x-www-form-urlencoded");
            s.send("disc="+t);
        }
        document.getElementById('load').className = 'load';
        setTimeout(isTimeOut,120000);
    }
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById("disc").value = "";
            document.getElementById("media").value = "";
            document.getElementById("desc").value = "";
            document.getElementById('load').className='hide';
            if(s.responseText.trim()===''){e.target.innerHTML = "Create Topic";}
            else{
                var st = s.responseXML;
                var id = st.querySelector("Discussion").getAttribute("id");
                var rcol = document.getElementById("rcol");
                var mdiv = document.getElementById('mdiv');
                var dd = document.getElementById('opdiv');
                if(mdiv!==null){
                    var ddiv = document.getElementById('d'+id);
                    if(ddiv!==null){
                        mdiv.removeChild(ddiv);
                        for(i=0;i<dd.childNodes.length;i++){
                            if(id===dd.childNodes[i].id){
                                dd.removeChild(dd.childNodes[i]);
                            }
                        }
                    }
                    if(!mdiv.hasChildNodes())
                        rcol.removeChild(mdiv);
                }
                var sugdiv = document.getElementById('sugdiv');
                if(sugdiv!==null){
                    sugdiv.removeChild(document.getElementById(id));
                    if(sugdiv.childElementCount===1){
                        sugdiv.parentNode.removeChild(sugdiv);
                    }
                }
                var disc = st.querySelector("Discussion");
                ul.appendChild(cd(disc));
                e.target.innerHTML = "Create Topic";
            }
        }
    };
}
function you(g,a){
    var figc = document.createElement('figcaption');
    s.open('post',a);
    s.setRequestHeader("content-type","application/x-www-form-urlencoded");
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4&&s.responseText!==""){
            figc.innerHTML = s.responseText; 
            g.appendChild(figc);
        }
    };
}
function cd(disc){
    var li = document.createElement("div");
    li.id = disc.getAttribute("id");
    li.className = 'w3-container w3-card-2 w3-white w3-round w3-margin';
    li.appendChild(document.createElement('br'));
    var ledit = document.createElement('span');
    ledit.textContent = 'Last edited - '+disc.querySelector('ledit').innerHTML;
    ledit.className = 'w3-right w3-opacity';
    li.appendChild(ledit);
    var dname = document.createElement("h2");
    dname.textContent=disc.querySelector("name").innerHTML;
    dname.id = 'name'+li.id;
    li.appendChild(dname);
    var edit = document.createElement("button");
    edit.innerHTML = "Edit";
    edit.id = li.id;
    edit.className ='edit';
    edit.addEventListener("click",edits);
    li.appendChild(edit);
    var chat = document.createElement("button");
    chat.id = li.id;
    chat.innerHTML = "Chat";
    chat.className="ch";
    chat.addEventListener("click",chats);
    li.appendChild(chat);
    var unsub = document.createElement("button");
    unsub.innerHTML = "Unsubscribe?";
    unsub.id = li.id;
    unsub.className = 'us';
    unsub.addEventListener("click",unsubs);
    li.appendChild(unsub);
    var desc = document.createElement("h3");
    desc.id = li.id + 'desc';
    desc.textContent = disc.querySelector("desc").innerHTML;
    if(disc.querySelector("desc").innerHTML===''){desc.className='hide';}
    li.appendChild(desc);
    if(disc.querySelector("dmedia")!==null){
        var m = disc.querySelector("dmedia").childNodes;
        if(m.length===1){
            var fig = document.createElement('figure');
            fig.className = 'fig';
            if(m[0].outerHTML.charAt(1)==='i'){
                var g = document.createElement("img");
                g.onload=function(){you(fig,g.src);};
            }
            else if(m[0].outerHTML.charAt(1)==='a'){
                var g = document.createElement("audio");
                g.controls = "controls";
                g.onloadeddata=function(){you(fig,g.src);};
            }
            else{
                var g = document.createElement("video");
                g.controls = "controls"; 
                g.onloadeddata=function(){you(fig,g.src);};
            }
            g.className = 'image';
            fig.id = li.id+'fig';
            g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
            fig.appendChild(g);
            li.appendChild(fig);            
        }else{
            if(window.innerWidth<769){
                var r = document.createElement('figure');
                r.className = 'fig';
                if(m[0].outerHTML.charAt(1)==='i'){
                    var g = document.createElement("img");
                    g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
                }else if(m[0].outerHTML.charAt(1)==='a'){
                    var g = document.createElement("audio");
                    g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
                }else{
                    var g = document.createElement("video");
                    g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
                }
                var figc = document.createElement('figcaption');
                figc.className = 'figcc';
                figc.innerHTML = m.length-1 + " more media";
                r.title = "Click to view more";
                r.onclick = function(event){
                    event.preventDefault();
                    st = '<head><link href=\"folder/css.css\" rel=\"stylesheet\"/></head><body>';
                    st += "<table class='table'>";
                    k=0;
                    for(i=0;i<Math.ceil(m.length/4);i++){
                        st += "<tr>";
                        for(j=0;j<4;j++){
                            var x = m[k];
                            if(x.outerHTML.charAt(1)==='i')
                                st += "<td><a href='/Trending/mediaservlet/"+x.innerHTML+"' > <img class='image' src='/Trending/mediaservlet/"+x.innerHTML+ "' /></a></td>";
                            else if(x.outerHTML.charAt(1)==='a')
                                st += "<td><a href='/Trending/mediaservlet/"+x.innerHTML+"' > <audio class='image' controls src='/Trending/mediaservlet/"+x.innerHTML+ "' ></audio></a></td>";
                            else
                                st += "<td><a href='/Trending/mediaservlet/"+x.innerHTML+"' > <video class='image' controls src=/Trending/mediaservlet/"+x.innerHTML+ "' ></video></a></td>";
                            k= k+1;if(k===m.length) break;
                        }
                        st += "</tr>";
                    }
                    st += "</table></body>";
                    var n = window.open();
                    var doc = n.document;
                    doc.open();
                    doc.write(st);
                    doc.close();
                };
                g.className = 'image';
                r.appendChild(g);
                r.appendChild(figc);
                li.appendChild(r);
            }else{
                k=0;
                var ta = document.createElement("table");
                ta.id = li.id+"table";
                ta.className = "table";   
                for(i=0;i<Math.ceil(m.length/3);i++){ 
                    var tr = ta.insertRow(i);
                    for(j=0;j<3;j++){
                        var x = m[k];
                        var td = tr.insertCell(j);
                        if(x.outerHTML.charAt(1)==='i'){
                            var r = document.createElement("a");
                            td.id = x.innerHTML.slice(x.innerHTML.indexOf('_')+1);
                            r.href = "/Trending/mediaservlet/"+x.innerHTML;
                            var g = document.createElement("img");
                            g.src = "/Trending/mediaservlet/"+x.innerHTML;
                            r.onclick = function(event){
                                event.preventDefault();
                                var nwin = window.open("","");
                                var fig = nwin.document.createElement('figure');
                                var gg = nwin.document.createElement('img');
                                gg.src = event.target.parentNode.href;
                                gg.onload= function(){you(fig,gg.src);};  
                                fig.appendChild(gg);
                                nwin.document.body.appendChild(fig);
                            };
                            r.title = "Click to view full size";
                            r.appendChild(g);
                            td.appendChild(r);
                        }else if(x.outerHTML.charAt(1)==='a'){
                            var g = document.createElement('figure');
                            var i=0;
                            var u = document.createElement("audio");
                            td.id = x.innerHTML.slice(x.innerHTML.indexOf('_')+1);
                            u.controls = "controls";
                            u.src = "/Trending/mediaservlet/"+x.innerHTML;
                            g.appendChild(u);
                            do{
                                you(g,u.src);
                                i = 1;
                            }while(i!==1)
                                td.appendChild(g);
                        }else{
                            var g = document.createElement('figure');
                            var i = 0;
                            var v = document.createElement("video");
                            td.id = x.innerHTML.slice(x.innerHTML.indexOf('_')+1);
                            v.controls = "controls";
                            v.src = "/Trending/mediaservlet/"+x.innerHTML;
                            g.appendChild(v);
                            do{
                                you(g,u.src);
                                i = 1;
                            }while(i!==1)                     
                                td.appendChild(g);
                        }
                        g.className = 'image';
                        k=k+1;
                        if(k===m.length) break;
                    }  
                }
                li.appendChild(ta);
            }
        }  
    }
    var hr = document.createElement('hr');
    hr.className = 'w3-clear';
    li.appendChild(hr);
    var datdiv = document.createElement("div");
    datdiv.id = 'datdiv'+li.id;
    datdiv.className='hide';
    if(disc.querySelector("Opinion")!==null){
        datdiv.className = 'w3-card-2 w3-round w3-white';
        var opi = disc.querySelectorAll("Opinion");
        for(var j = 0;j<opi.length;j++){
            var copinion = opi[j];
            var t = op(copinion,li.id);
            datdiv.appendChild(t);
        }
    }
    li.appendChild(datdiv);
    var di = document.createElement("div");
    di.className = "tarea";
    var tarea = document.createElement("textArea");
    tarea.id='t'+li.id;
    tarea.focus();
    tarea.placeholder = "Add an Opinion";
    tarea.addEventListener("keypress",addOpinion);
    di.appendChild(tarea);
    var addmedia = document.createElement("input");
    addmedia.type = "file";
    addmedia.multiple = "multiple";
    addmedia.id = 'm'+li.id;
    addmedia.onchange = vChange;
    di.appendChild(addmedia);
    var addbutton = document.createElement("button");
    addbutton.innerHTML = "Add Opinion";
    addbutton.className = "bt";
    addbutton.id = 'b'+li.id;
    addbutton.addEventListener("click",addOpinion);
    di.appendChild(addbutton);
    li.appendChild(di);
    if(disc.querySelector("sug")!==null){
        var relDiscs = disc.querySelectorAll("sug");
        var sugDiv = document.createElement("div");
        sugDiv.id = "sugdiv";
        var pre = document.createElement("span");
        pre.id = 'p';
        pre.innerHTML = "Related Discussions";
        sugDiv.appendChild(pre);
        for(i=0;i<relDiscs.length;i++){
            var sdiv = document.createElement("div");
            sdiv.className = 'sdiv';
            var but = document.createElement("button");
            var pr = document.createElement("span");
            pr.textContent = relDiscs[i].innerHTML;
            but.id = relDiscs[i].innerHTML;
            sdiv.id = but.id;
            but.innerHTML = "Subscribe?";
            but.addEventListener("click",subs);
            sdiv.appendChild(pr);
            sdiv.appendChild(but);
            sugDiv.appendChild(sdiv);
        }
        li.appendChild(sugDiv);
    }
    return li;
}
function loads(){
    var u = document.getElementById("ul");
    s.open("get","/Trending/welcome?t");
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            var st = s.responseXML;
            var discs = st.querySelectorAll("disc");
            for(i=0;i<discs.length;i++){
                var li = document.createElement("li");
                var h = document.createElement("h5");
                h.textContent = discs[i].innerHTML;
                li.appendChild(h);
                u.appendChild(li);
            }
        }    
    };
}
function load(){ 
    var st = createXHR(); 
    st.open("get","/Trending/welcome?n");
    st.send(null);
    st.onreadystatechange = function(){
        if(st.readyState===4){
            ss = st.responseText.split(':');
            name = ss[0];
            ws = new WebSocket("ws://localhost:8080/chat/"+ss[0]);
            ws.onmessage = rMsg;
            if(ss[1].trim() !== ""){
                var sp = document.createElement('span');
                sp.innerHTML = ss[1];
                sp.className = 'w3-badge w3-right w3-small w3-green';
                var amsg = document.getElementById('amsg');
                amsg.appendChild(sp);
            }
        }
    };
    ul = document.getElementById('ul');
    document.getElementById("disc").focus();
    s.open("get","/Trending/welcome");
    s.send(null);
    s.onreadystatechange = changeAll;
}
function changeAll(){
    if(s.readyState===4){
        var st = s.responseXML; 
        ul.innerHTML = '';
        var l = document.getElementById('load');  
        var rcol = document.getElementById('rcol');
        rcol.innerHTML = '';
        l.className="hide";
        if(st.querySelector("news")!==null){
            if(st.querySelector('ad')!==null){
                var allad = st.querySelectorAll('ad');
                mdiv = document.createElement('div');
                mdiv.id = 'mdiv';
                mdiv.className = 'w3-card-2 w3-round w3-white w3-padding-16 w3-center';
                for(i=0;i<allad.length;i++){
                    div = document.createElement('div');
                    div.id = 'd'+allad[i].innerHTML;
                    div.className = 'dcol';
                    sspan = document.createElement('button');
                    sspan.id = allad[i].innerHTML;
                    sspan.onclick = subs;
                    sspan.textContent = 'SUBSCRIBE?';
                    dspan = document.createElement('span');
                    dspan.textContent = allad[i].innerHTML;
                    div.appendChild(dspan);
                    div.appendChild(sspan);
                    mdiv.appendChild(div);
                }
                rcol.appendChild(mdiv);
            }
            if(st.querySelector('op')!==null){
                rcol.appendChild(document.createElement('br'));
                var dd = document.createElement('div');
                dd.id = 'opdiv';
                var allop = st.querySelectorAll('Opinion');
                for(i=0;i<allop.length;i++){
                    var d = document.createElement('div');
                    var id = allop[i].getAttribute('id');
                    d.id = id.slice(0,id.indexOf('_'));
                    d.className = 'w3-card-2 w3-round w3-white w3-center';
                    var cont = document.createElement('div');
                    cont.class = 'w3-container';
                    cont.appendChild(op(allop[i],'ohh'));
                    d.appendChild(cont);
                    dd.appendChild(d);
                    dd.appendChild(document.createElement('br'));
                }
                rcol.appendChild(dd);
            }
        }
        var discs = st.querySelectorAll("Discussion");
        for(var i = 0;i<discs.length;i++){
           ul.appendChild(cd(discs[i]));
        }
        window.onbeforeunload = function(){
            ws.close();
           //return 'ok';
        };
    }
}
function addOpinion(e){
    var id = e.target.id.slice(1);
    if(e.key==="Enter"){
        var node = e.target;
        var opi = node.value.trim();
        if(opi.length>0){
            node.value="".trim();
            s.open("post","/Trending/welcome/i");
            document.getElementById('load').className='load';
            setTimeout(isTimeOut,120000);
            s.setRequestHeader("content-type","application/x-www-form-urlencoded");
            s.send("id="+id+"&opi="+opi);
        }
    }else if(e.type==="click"){
        var m = document.getElementById('m'+id);
        var opi = document.getElementById('t'+id).value.trim();
        document.getElementById('t'+id).value = "";
        if(m.value!==""&&opi.length>0){
            e.target.innerHTML = "Adding Opinion...";
            e.target.className = "click";
            e.target.disabled = "disabled";
            var d = new FormData();
            d.append('&',id);
            d.append('%',opi);
            var tf = jj['tf'];
            var z = jj['z'];
            for(i=0;i<tf.length;i++){
                d.append(tf[i],z[i]);
            }
            s.open("post","/Trending/welcome/id");
            s.send(d);
        }else if(opi.length>0){
            s.open("post","/Trending/welcome/i");
            s.setRequestHeader("content-type","application/x-www-form-urlencoded");
            s.send("id="+id+"&opi="+opi);   
        }
        document.getElementById('load').className='load';
        setTimeout(isTimeOut,120000);   
    }
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className='hide';
            if(e.type==="click"){
                e.target.innerHTML = "Add Opinion";
                e.target.className = "bt";
                m.value = '';
            }
            var st = s.responseXML;
            var opinion = st.querySelector("Opinion");
            var t = op(opinion);
            var datdiv = document.getElementById('datdiv'+id);
            datdiv.className='w3-card-2 w3-round w3-white';
            datdiv.appendChild(t);
        }
    };
}
function op(copinion){
    var id = copinion.getAttribute("id");
    var div = document.createElement("div");
    div.id = id;
    div.className = "opclass";
    var msg = document.createElement("p");
    msg.textContent=copinion.querySelector("msg").innerHTML;
    div.appendChild(msg);
    var edit = document.createElement("button");
    edit.innerHTML = "Edit";
    edit.className = 'editop';
    edit.id = id;
    edit.addEventListener("click",edi);
    div.appendChild(edit);
    if(copinion.querySelector("media").childNodes.length>0){
        var m = copinion.querySelector("media").childNodes;
        if(m.length===1){
            var fig = document.createElement('figure');
            fig.className = 'fig';
            if(m[0].outerHTML.charAt(1)==='i'){
                var g = document.createElement("img");
                g.onload=function(){you(fig,g.src);};
            }
            else if(m[0].outerHTML.charAt(1)==='a'){
                var g = document.createElement("audio");
                g.controls = "controls";
                g.onloadeddata=function(){you(fig,g.src);};
            }
            else{
                var g = document.createElement("video");
                g.controls = "controls"; 
                g.onloadeddata=function(){you(fig,g.src);};
            }
            g.className = 'image';
            fig.id = id+'fig';
            g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
            fig.appendChild(g);
            div.appendChild(fig);            
        }else{
            if(window.innerWidth<769||arguments[1]==='yes'){
                var r = document.createElement('figure');
                r.className = 'fig';
                if(m[0].outerHTML.charAt(1)==='i'){
                    var g = document.createElement("img");
                    g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
                }else if(m[0].outerHTML.charAt(1)==='a'){
                    var g = document.createElement("audio");
                    g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
                }else{
                    var g = document.createElement("video");
                    g.src = "/Trending/mediaservlet/"+m[0].innerHTML;
                }
                var figc = document.createElement('figcaption');
                figc.innerHTML = m.length-1 + " more media";
                figc.className = 'figcc';
                r.onclick = function(event){
                    event.preventDefault();
                    st = '<head><link href=\"folder/css.css\" rel=\"stylesheet\"/></head><body>';
                    st += "<table class='table'>";
                    k=0;
                    for(i=0;i<Math.ceil(m.length/4);i++){
                        st += "<tr>";
                        for(j=0;j<4;j++){
                            var x = m[k];
                            if(x.outerHTML.charAt(1)==='i')
                                st += "<td><a href='/Trending/mediaservlet/"+x.innerHTML+"' > <img class='image' src='/Trending/mediaservlet/"+x.innerHTML+ "' /></a></td>";
                            else if(x.outerHTML.charAt(1)==='a')
                                st += "<td><a href='/Trending/mediaservlet/"+x.innerHTML+"' > <audio class='image' controls src='/Trending/mediaservlet/"+x.innerHTML+ "' ></audio></a></td>";
                            else
                                st += "<td><a href='/Trending/mediaservlet/"+x.innerHTML+"' > <video class='image' controls src=/Trending/mediaservlet/"+x.innerHTML+ "' ></video></a></td>";
                            k= k+1;if(k===m.length) break;
                        }
                        st += "</tr>";
                    }
                    st += "</table></body>";
                    var n = window.open();
                    var doc = n.document;
                    doc.open();
                    doc.write(st);
                    doc.close();
                };
                g.className = 'image';
                r.appendChild(g);
                r.appendChild(figc);
                div.appendChild(r);
            }else{
                k=0;
                var ta = document.createElement("table");
                ta.id = id+"table";
                ta.className = "table";   
                for(i=0;i<Math.ceil(m.length/3);i++){ 
                    var tr = ta.insertRow(i);
                    for(j=0;j<3;j++){
                        var x = m[k];
                        var td = tr.insertCell(j);
                        if(x.outerHTML.charAt(1)==='i'){
                            var r = document.createElement("a");
                            td.id = x.innerHTML.slice(x.innerHTML.indexOf('_')+1);
                            r.href = "/Trending/mediaservlet/"+x.innerHTML;
                            var g = document.createElement("img");
                            g.src = "/Trending/mediaservlet/"+x.innerHTML;
                            r.onclick = function(event){
                                event.preventDefault();
                                var nwin = window.open("","");
                                var fig = nwin.document.createElement('figure');
                                var gg = nwin.document.createElement('img');
                                gg.src = event.target.parentNode.href;
                                gg.onload= function(){you(fig,gg.src);};  
                                fig.appendChild(gg);
                                nwin.document.body.appendChild(fig);
                            };
                            r.title = "Click to view full size";
                            r.appendChild(g);
                            td.appendChild(r);
                        }else if(x.outerHTML.charAt(1)==='a'){
                            var g = document.createElement('figure');
                            var i=0;
                            var u = document.createElement("audio");
                            td.id = x.innerHTML.slice(x.innerHTML.indexOf('_')+1);
                            u.controls = "controls";
                            u.src = "/Trending/mediaservlet/"+x.innerHTML;
                            g.appendChild(u);
                            do{
                                you(g,u.src);
                                i = 1;
                            }while(i!==1)
                            td.appendChild(g);
                        }else{
                            var g = document.createElement('figure');
                            var i = 0;
                            var v = document.createElement("video");
                            td.id = x.innerHTML.slice(x.innerHTML.indexOf('_')+1);
                            v.controls = "controls";
                            v.src = "/Trending/mediaservlet/"+x.innerHTML;
                            g.appendChild(v);
                            do{
                                you(g,u.src);
                                i = 1;
                            }while(i!==1)                     
                            td.appendChild(g);
                        }
                        g.className = 'image';
                        k=k+1;
                        if(k===m.length) break;
                    }  
                } 
                div.appendChild(ta);
            }
        }  
    }
    var otable = document.createElement("table");
    otable.className = 'ot';
    otable.id = 'otable'+id;
    adiv = otable.insertRow(0); 
    adiv.id = "adiv";
    acell = adiv.insertCell();
    var atd = document.createElement("span");
    atd.innerHTML = copinion.querySelector("agree").innerHTML+" Agreed";
    atd.id = "a"+id;
    acell.appendChild(atd);
    dcell = adiv.insertCell();
    var dtd = document.createElement("span");
    dtd.innerHTML = copinion.querySelector("disagree").innerHTML+" Disagreed";
    dtd.id = "d"+id;
    dcell.appendChild(dtd); 
    gcell = adiv.insertCell();
    var a = document.createElement("a");
    a.href="";    
    if(copinion.querySelector("comments")!==null){
        a.innerHTML = copinion.querySelector("comments").innerHTML;
        a.addEventListener("click",getComments);    
    } 
    a.id = 'g'+id;
    gcell.appendChild(a);
    if(arguments[1]==='ohh'){}
    else{
    var dd = otable.insertRow(1);
    dd.id = "dd";
    ab = dd.insertCell(0);
    var abutton = document.createElement("button");
    abutton.innerHTML = "Agree";
    abutton.setAttribute("aid",'a'+id);
    if(copinion.querySelector("aset").innerHTML==="true"){
        abutton.addEventListener("click",aagree);
        atd.className = "click";
    }
    else
        abutton.addEventListener("click",agree);
    ab.appendChild(abutton);
    db = dd.insertCell(1);
    var dbutton = document.createElement("button");
    dbutton.innerHTML = "Disagree";
    dbutton.setAttribute("did",'d'+id);
    if(copinion.querySelector("dset").innerHTML==="true"){
        dbutton.addEventListener("click",dagree);
        dtd.className = "click";
    }
    else
        dbutton.addEventListener("click",disagree);
    db.appendChild(dbutton);
    cb = dd.insertCell(2);
    var cbutton = document.createElement("button");
    cbutton.innerHTML = "Add Comments";
    cbutton.setAttribute("cid",id);
    cbutton.addEventListener("click",addComment);
    cb.appendChild(cbutton);}
    div.appendChild(otable);
    return div;
}
function agree(e){
    var a = e.target;
    var aid = a.getAttribute("aid");
    var ag = document.getElementById(aid);
    var msg = ag.innerHTML.split(" ")[0];
    msg = Number(msg) + 1;
    ag.className = "click";
    ag.innerHTML = msg + " Agreed";
    list.push(ag);
    a.removeEventListener("click",agree);
    a.addEventListener("click",aagree);
    e.preventDefault();
}
function aagree(e){
    var a = e.target;
    var aid = a.getAttribute("aid");
    var ag = document.getElementById(aid);
    ag.className = "";
    var msg = ag.innerHTML.split(" ")[0];
    msg = Number(msg)-1;
    ag.innerHTML = msg + " Agreed";
    list.push(ag);
    a.removeEventListener("click",aagree);
    a.addEventListener("click",agree);
    e.preventDefault();
}
function disagree(e){
    var d = e.target;
    var did = d.getAttribute("did");
    var dg = document.getElementById(did);
    dg.className = "click";
    var msg = dg.innerHTML.split(" ")[0];
    msg = Number(msg) + 1;
    dg.innerHTML = msg + " Disagreed";
    lists.push(dg);
    d.removeEventListener("click",disagree);
    d.addEventListener("click",dagree);
    e.preventDefault();
}
function dagree(e){
    var d = e.target;
    var did = d.getAttribute("did");
    var dg = document.getElementById(did);
    dg.className = "";
    var msg = dg.innerHTML.split(" ")[0];
    msg = Number(msg)-1;
    dg.innerHTML = msg + " Disagreed";
    lists.push(dg);
    d.removeEventListener("click",dagree);
    d.addEventListener("click",disagree);
    e.preventDefault();
}
function addComment(e){
    var acNode = e.target;
    var d = acNode.getAttribute("cid");
    var o = d.slice(d.indexOf('_')+1);
    var id = d.slice(0,d.indexOf('_'));
    var opi = document.getElementById(d);
    var div = document.createElement("div");
    div.id='div';
    di = document.createElement("div");
    di.id='w'+d;
    div.className = "addCom";
    var t = document.createElement("textarea");
    t.id = "ta";
    var tm = document.createElement("input");
    var sub = document.createElement("button");
    sub.innerHTML = "Comment";
    tm.type = "file";
    tm.multiple = "multiple";
    div.appendChild(t);
    div.appendChild(tm);
    div.appendChild(sub);
    t.addEventListener("keypress",function(e){
        if(e.key==="Enter"&&t.value.length>0){
            document.getElementById('load').className='load';
            var msg = e.target.value;
            s.open("post","/Trending/welcome/c");
            s.setRequestHeader("content-type","application/x-www-form-urlencoded");
            s.send('id='+id+'&msg='+msg+'&o='+o);
            div.style.display = "none";
            opi.removeChild(div);
            acNode.addEventListener("click",addComment);
        }
    });
    sub.addEventListener("click",function(){
        var com = document.getElementById("ta").value;
        var mv = tm.value;
        if(mv!==""&&com.length>0){
            var t = tm.files;
            var d = new FormData();
            d.append('o',o);
            d.append('com',com);
            d.append('id',id);
            for(i=0;i<t.length;i++){
                d.append('j'+Math.random()*10E5,t[i]);
            }
            s.open("post","/Trending/welcome/m");
            s.send(d);
        }else if(com.length>0){
            s.open("post","/Trending/welcome/c");
            s.setRequestHeader("content-type","application/x-www-form-urlencoded");
            s.send('id='+id+'&msg='+com+'&o='+o);
        }
        div.style.display = "none";
        opi.removeChild(div);
        document.getElementById('load').className='load';
        acNode.addEventListener("click",addComment);
    });
    var btn = document.createElement("button");
    btn.innerHTML = "close";
    btn.id = 'c'+d;
    btn.addEventListener("click",clos);
    btn.className = "clos";
    opi.appendChild(btn);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className='hide';
            var st = s.responseXML;
            var opp = st.querySelector("Opinion");
            di.appendChild(op(opp));
            opi.appendChild(di);
        }  
    };
    acNode.removeEventListener("click",addComment);
    acNode.addEventListener("click",function(){t.focus();});
    opi.appendChild(div);
    t.focus();
    e.preventDefault();
}
function getComments(e){
    var i = e.target.id.slice(1);
    var opi = document.getElementById(i);
    var di = document.createElement("div");
    di.id='w'+i;
    var btn = document.createElement("button");
    btn.innerHTML = "close";
    btn.id = 'c'+i;
    btn.addEventListener("click",clos);
    btn.className = "clos";
    opi.appendChild(btn);
    s.open("post","/Trending/welcome/g");
    document.getElementById('load').className='load';
    setTimeout(isTimeOut,120000);
    s.setRequestHeader("content-type","application/x-www-form-urlencoded");
    s.send('id='+i.slice(i.indexOf('_')+1));
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className='hide';
            var st = s.responseXML;
            var opis = st.querySelectorAll("Opinion");     
            for(var i=0;i<opis.length;i++){
                var copinion = opis[i];
                var table = op(copinion);
                di.appendChild(table); 
            }
            opi.appendChild(di);
            e.target.removeEventListener("click",getComments);
            e.target.addEventListener("click",function(e){e.preventDefault();});
        }
    };
    e.preventDefault();
}
function clos(e){
    var b = e.target.id.slice(1);
    var o = document.getElementById(b);
    s.open("post","/Trending/welcome/j"+b);
    s.setRequestHeader("content-type","application/x-www-form-urlencoded");
    var wrap = document.getElementById('w'+b);
    var clos = document.getElementById(e.target.id);
    if(wrap!==null){
    var ops = wrap.childNodes;
    var li = {};
    for(i=0;i<ops.length;i++){
        li[ops[i].id] = ops[i].getElementsByTagName("span")[0].innerHTML.split(" ")[0]+'_'+ops[i].getElementsByTagName("span")[1].innerHTML.split(" ")[0];
    }
    var js = JSON.stringify(li);
    s.send("js="+js);
    o.removeChild(wrap);}
    else
        o.removeChild(document.getElementById('div'));
    o.removeChild(clos);
    s.onreadystatechange = function(){
        if(s.readyState===4){ 
            st = s.responseXML;
            comm = st.querySelector('comments').innerHTML;
            var div = document.getElementById('g'+b);
            div.innerHTML = comm;
            document.getElementById('g'+b).addEventListener("click",getComments);
        }
    };
}
function logout(){
    var li = {};
    var ll = {};
    var js,jss;
    ws.close();
    s.open("post","/Trending/welcome/o");
    s.setRequestHeader("content-type","application/x-www-form-urlencoded");
    if(list.length!==0&&lists.length!==0){
        for(i=0;i<list.length;i++){
            var o = list[i];
            ms = o.innerHTML.charAt(0);
            li[o.id.slice(1)] = ms;
        }
        js = JSON.stringify(li);
        for(j=0;j<lists.length;j++){
            var o = lists[j];
            ms = o.innerHTML.charAt(0);
            ll[o.id.slice(1)] = ms;
        } 
        jss = JSON.stringify(ll);
        s.send("js="+js+"&jss="+jss);
    }else if(list.length!==0&&lists.length===0){
        for(i=0;i<list.length;i++){
            var o = list[i];
            ms = o.innerHTML.charAt(0);
            li[o.id.slice(1)] = ms;
        }
        js = JSON.stringify(li);
        s.send("js="+js);
    }else if(lists.length!==0&&list.length===0){
        for(i=0;i<lists.length;i++){
            var o = lists[i];
            ms = o.innerHTML.charAt(0);
            li[o.id.slice(1)] = ms;
        }
        js = JSON.stringify(li);
        s.send("jss="+js);
    }else{
        s.send(null);
    }
    s.onreadystatechange = function(){
        if(s.readyState===4){
            location.href = "index.html";
        }
    };
}
function activities(){
    var ac = document.getElementById("ms");
    if(ac.className==="hide"){
        s.open("get","/Trending/welcome/acti");
        document.getElementById('load').className='load';
        s.send(null);
        s.onreadystatechange = function(){
            if(s.readyState===4){
                document.getElementById('load').className='hide';
                ac.innerHTML="";
                ac.className = 'ms';
                var st = s.responseXML;
                var acti = st.querySelectorAll("activities");
                for(i=0;i<acti.length;i++){
                    var li = document.createElement("li");
                    var h5 = document.createElement("h5");
                    h5.innerHTML = acti[i].innerHTML;
                    li.appendChild(h5);
                    ac.appendChild(li);
                }
                if(document.getElementById('xid')!==null){
                    document.getElementById('snib').removeChild(document.getElementById('xid'));
                }
            }  
        };
    }
    else
        ac.className = "hide";
}
function unsubs(e){
    var id = e.target.id;
    s.open("get","/Trending/welcome?s="+id);
    document.getElementById('load').className='load';
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className='hide';
            var d = document.getElementById(id);
            var p = d.parentNode;
            p.removeChild(d);
        }
    };
    e.preventDefault();
}
 function edits(e){
    var id = e.target.id;
    s.open("get","/Trending/welcome?e="+id);
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            if(s.responseText.trim()!==""){
                var li = document.getElementById(id);
                var dname = document.getElementById('name'+id);
                dname.contentEditable = 'true';
                dname.focus();
                var desc = document.getElementById(id+'desc');
                var edit = e.target;
                edit.innerHTML = "Save Changes?";
                edit.removeEventListener("click",edits);
                edit.addEventListener("click",save);
                desc.className = "input";
                desc.contentEditable = "true";
                desc.title = 'What is this topic about?';
                var table = document.getElementById(id+'table');
                var fig = document.getElementById(id+'fig');
                if(table!==null){
                    var tds = table.querySelectorAll('td');
                    for(i=0;i<tds.length;i++){
                        var c = document.createElement("input");
                        c.type = "checkbox";
                        c.addEventListener("click",clicked);
                        tds[i].appendChild(c);
                    }
                }else if(fig!==null){
                    var c = document.createElement("input");
                    c.type = "checkbox";
                    c.addEventListener("click",clicked);
                    fig.appendChild(c);
                }
                var md = document.createElement("div");
                md.id = 'md';
                var more = document.createElement("pre");
                var but = document.createElement("input");
                but.type = "file";
                but.multiple = "multiple";
                but.id = 'n'+id;
                but.onchange = vChange;
                more.innerHTML = "Add Media?";
                md.appendChild(more);
                md.appendChild(but);
                li.insertBefore(md,desc);
            }else
                alert("You can't edit this Discussion");
        }
    };
    e.preventDefault();
}
function clicked(e){
    var td = e.target.parentNode;
    td.removeChild(td.firstChild);
    l.push(td.id);
    td.removeChild(e.target);
}
function clicke(e){
    var td = e.target.parentNode;
    td.removeChild(td.firstChild);
    li.push(td.id);
    td.removeChild(e.target);
}
function save(e){
    var id = e.target.id;
    var li = document.getElementById(id);
    var name = document.getElementById('name'+id).textContent;
    var med = document.getElementById('n'+id);
    var desc = document.getElementById(id+'desc').textContent;
    var fd = new FormData();
    fd.append('$',id);
    fd.append('*',name);
    fd.append('%',desc);
    if(l.length!==0){
        for(i=0;i<l.length;i++){
            var im = l[i];
            fd.append('&'+im,im);
        }l.length=0;
    }
    if(med.value!==""){
        var tf = jj['tf'];
        var z = jj['z'];
        for(i=0;i<tf.length;i++){
            fd.append(tf[i],z[i]);
        }
    }
    s.open("post","/Trending/welcome/b");
    document.getElementById('load').className='load';
    setTimeout(isTimeOut,120000);
    s.send(fd);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className='hide';
            var st = s.responseXML;
            var disc = st.querySelector("Discussion");
            ul.replaceChild(cd(disc),li);
        }
    };
    e.preventDefault();
}
function edi(e){
    var n = e.target;
    var id = n.id;
    s.open("get","/Trending/welcome?v="+id.slice(id.indexOf('_')+1));
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            if(s.responseText.trim()!==""){
                var opi = n.previousSibling;
                opi.contentEditable = "true";
                opi.focus();
                n.innerHTML = "Save Changes?";
                n.removeEventListener("click",edi);
                n.addEventListener("click",sav);
                var table = document.getElementById(id+'table');
                var fig = document.getElementById(id+'fig');
                var ot = document.getElementById('otable'+id);
                if(table!==null){
                    var tds = table.querySelectorAll('td');
                    for(i=0;i<tds.length;i++){
                        var c = document.createElement("input");
                        c.type = "checkbox";
                        c.addEventListener("click",clicke);
                        tds[i].appendChild(c);
                    }
                }else if(fig!==null){
                    var c = document.createElement("input");
                    c.type = "checkbox";
                    c.addEventListener("click",clicked);
                    fig.appendChild(c);
                }
                var md = document.createElement('div');
                var more = document.createElement("pre");
                var but = document.createElement("input");
                but.type = "file";
                but.multiple = "multiple";
                but.id = "h"+id;
                but.onchange=vChange;
                more.innerHTML = "Add Media?";
                md.appendChild(more);
                md.appendChild(but);
                n.parentNode.insertBefore(md,ot);
            }
        }
    };
    e.preventDefault();
}
function sav(e){
    var id = e.target.id;
    var med = document.getElementById('h'+id);
    var o = document.getElementById(id);
    var name = o.firstChild;
    var fd = new FormData();
    fd.append('$',id.slice(id.indexOf('_')+1));
    fd.append('%',name.textContent);
    if(li.length!==0){
       for(i=0;i<li.length;i++){
            var im = li[i];
            fd.append('&'+im,im);
        }
        li.length=0;
    }
    if(med.value!==""){
        var tf = jj['tf'];
        var z = jj['z'];
        for(i=0;i<tf.length;i++){
            fd.append(tf[i],z[i]);
        }
    }
    s.open("post","/Trending/welcome/w");
    document.getElementById('load').className='load';
    s.send(fd);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className = 'hide';
            var st = s.responseXML;
            var opi = st.querySelector("Opinion");
            var datdiv = document.getElementById(o.parentNode.id);
            var t = op(opi);
            datdiv.replaceChild(t,o);
        }
    };
    e.preventDefault();
}
function discsub(e){
    var di = e.target.id;
    s.open("get","/Trending/welcome/discsub/"+di);
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            e.target.innerHTML = s.responseText;
            e.target.disabled = "disabled";
        }
    };
}
function subs(e){
    var di = e.target.id;
    document.getElementById('load').className = 'load';
    s.open("get","/Trending/welcome/sub/"+di);
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className = 'hide';
            if(s.responseText.trim()===""){
                e.target.innerHTML = "Already Subscribed";
                e.target.disabled = "disabled";
            }
            else{
                var st = s.responseXML;
                var id = st.querySelector("Discussion").getAttribute("id");
                var rcol = document.getElementById("rcol");
                var mdiv = document.getElementById('mdiv');
                var dd = document.getElementById('opdiv');
                if(mdiv!==null){
                    var ddiv = document.getElementById('d'+id);
                    if(ddiv!==null){
                        mdiv.removeChild(ddiv);
                        for(i=0;i<dd.childNodes.length;i++){
                            if(id===dd.childNodes[i].id){
                                dd.removeChild(dd.childNodes[i]);
                            }
                        }
                    }
                    if(!mdiv.hasChildNodes())
                        rcol.removeChild(mdiv);
                }
                var sugdiv = document.getElementById('sugdiv');
                if(sugdiv!==null){
                    sugdiv.removeChild(document.getElementById(id));
                    if(sugdiv.childElementCount===1){
                        sugdiv.parentNode.removeChild(sugdiv);
                    }
                }
                var disc = st.querySelector("Discussion");
                ul.appendChild(cd(disc));
            }
        }
    };
    e.preventDefault();
}
function search(e){
    var d;
    if(e.key==='Enter'){
        d = e.target.value;
    }else if(e.type==='click'){
        d = document.getElementById('search').value;
    }else return;
    s.open("get","/Trending/welcome/"+d);
    document.getElementById('load').className='load';
    s.send(null);
    s.onreadystatechange = function(){
        if(s.readyState===4){
            document.getElementById('load').className='hide';
            var st = s.responseText;
            if(st.trim()===''){
                var out = document.getElementById('out');
                out.innerHTML = "Discussion doesn't exist";
                out.className='';
            }else{
                var n = window.open();
                var doc = n.document;
                doc.open();
                doc.write(st);
                doc.close();
            }
        }
    };
}
function chats(e){
    ch = e.target.id;
    var dv = document.getElementById(ch+'dv');
    if(dv!==null){
        document.getElementById(ch).removeChild(dv);
    }
    else{
        dv = document.createElement("div");
        dv.id = ch+'dv';
        dv.className = "w3-card-2 chat";
        s.open("get","/Trending/welcome/chat/"+ch);
        s.send(null);
        s.onreadystatechange = function(){
            if(s.readyState===4){
                var st = s.responseXML;
                var users = st.querySelectorAll("user");
                    for(i=0;i<users.length;i++){
                        online = users[i].getAttribute("online");
                        b = users[i].innerHTML;
                        var te = document.createElement("div");
                        var a = document.createElement("a");
                        a.href="";
                        a.id = b;
                        te.id = b;
                        a.innerHTML = b;
                        ball = document.createElement('span');
                        ball.innerHTML = 'o';
                        if(online==="online"){    
                            ball.id = 'onball';
                            a.addEventListener("click",chat);
                        }else{
                            ball.id = 'offball';
                            a.addEventListener('click',chatoff);
                        }
                        te.appendChild(a);
                        te.appendChild(ball);
                        dv.appendChild(te);
                    }
                    document.getElementById(ch).insertBefore(dv,document.getElementById(ch+'desc'));
                }
            };
        }
    e.preventDefault();
}
function chat(e){
    if(ws){
    var dv = document.getElementById(ch+'dv');
    document.getElementById(ch).removeChild(dv);
    var a = e.target;
    var d = document.getElementById(a.id+'d');
    if(d===null){
        d = document.createElement('div');
        d.id = a.id + 'd'; 
        d.className = 'hat';
    }
    var closebtn = document.getElementById(a.id+'x');
    if(closebtn===null){
        closebtn = document.createElement('button');
        closebtn.innerHTML = 'X';
        closebtn.id = a.id+'x';
        closebtn.className='closebtn';
    }
    closebtn.onclick = function(e){
        document.body.removeChild(e.target.parentNode);
    };
    d.appendChild(closebtn);
    var cs = document.getElementById(a.id+'cs');
    if(cs===null){
        cs = document.createElement('div');
        cs.id = a.id + 'cs';
        cs.className = 'chatspace';
    }
    d.appendChild(cs);
    var ta = document.getElementById(a.id+'ta');
    if(ta===null){
        ta = document.createElement('input');
        ta.type='text';
        ta.className = 'enter';
        ta.id = a.id + 'ta';
        ta.placeholder = 'Press Enter to send';
    }
    var bt = document.getElementById(a.id+'bt');
    if(bt===null){
        bt = document.createElement("input");
        bt.value = "Send";
        bt.type = 'submit';
        bt.id = a.id + 'bt';
    }
    ta.focus();
    d.appendChild(ta);
    d.appendChild(bt);
    ta.addEventListener("keypress",function(e){
        if(e.key==="Enter"&&ta.value.trim()!==""){
            v = ta.value;
            var nm = document.createElement('span');
            var nam = document.createElement('span');
            nam.innerHTML = name;
            nam.className='nam';
            nm.appendChild(nam);
            nm.appendChild(document.createTextNode(':'+v));
            var cs = document.getElementById(a.id+'cs');
            if(cs===null){
                cs = document.createElement('div');
                cs.id = a.id + 'cs';
                cs.className = 'chatspace';
            }
            cs.appendChild(nm); 
            cs.appendChild(document.createElement('br'));
            var msg = {"sender":name,"receiver":a.id,"msg":v,"disc":ch};
            ws.send(JSON.stringify(msg));
            ta.placeholder='Press Enter to send';
            ta.value='';
        }
    });
    bt.onclick = function(e){
        v = ta.value;
        var nm = document.createElement('span');
        var nam = document.createElement('span');
        nam.innerHTML = name;
        nam.className='nam';
        nm.appendChild(nam);
        nm.appendChild(document.createTextNode(':'+v));
        var cs = document.getElementById(a.id+'cs');
            if(cs===null){
                cs = document.createElement('div');
                cs.id = a.id + 'cs';
                cs.className = 'chatspace';
            } 
        cs.appendChild(nm); 
        cs.appendChild(document.createElement('br'));
        var msg = {"sender":name,"receiver":a.id,"msg":v,"disc":ch};
        ws.send(JSON.stringify(msg)); 
        ta.placeholder='Press Enter to send';
        ta.value='';
        e.preventDefault();
    };
    document.body.insertBefore(d,document.getElementById('ifr'));
    e.preventDefault();
    }else{ alert('Upgrade your browser to the latest version');
        a.removeEventListener('click',chat);
        a.addEventListener('click',chatoff);
    }
}
function rMsg(evt){
    if(ws.readyState===1){
        var st = evt.data;
        if(st.charAt(0)==='$'){
            var snib = document.getElementById('snib');
            if(document.getElementById('xid')===null){
                var x = document.createElement('span');
                x.innerHTML = '*';
                x.id = 'xid';
                snib.appendChild(x);
            }
        }else{
        var stt = st.split(':');
        ch = stt[0];  
        var d = document.getElementById(stt[1]+'d');
        if(d===null){
            d = document.createElement('div');
            d.id = stt[1]+'d';
            d.className = 'hat';
        }
        var closebtn = document.getElementById(stt[1]+'x');
        if(closebtn===null){
            closebtn = document.createElement('button');
            closebtn.innerHTML = 'X';
            closebtn.id = stt[1]+'x';
            closebtn.className='closebtn';
        }
        closebtn.onclick = function(e){
            document.body.removeChild(e.target.parentNode);
        };
        d.appendChild(closebtn);
        var cs = document.getElementById(stt[1]+'cs');
        if(cs===null){
            cs = document.createElement('div');
            cs.className = 'chatspace';
            cs.id = stt[1] + 'cs';
        } 
        var cbox = document.getElementById(stt[1] +'ta'); 
        if(cbox===null){ 
            cbox = document.createElement('input');
            cbox.id = stt[1] + 'ta';
            cbox.type = 'text';
            cbox.placeholder = 'Press Enter to send';
            cbox.className='enter';
        }
        var nm = document.createElement('span');
        var nam = document.createElement('span');
        nam.innerHTML=stt[1];
        nam.className='nnam';
        nm.appendChild(nam);
        nm.appendChild(document.createTextNode(':'+stt[2]));
        nm.className='nm';
        cs.appendChild(nm); 
        cs.appendChild(document.createElement('br'));
        d.appendChild(cs);
        cbox.focus();
        cbox.addEventListener("keypress",function(e){
            if(e.key==="Enter"&&cbox.value.trim()!==""){
                var nm = document.createElement('span');
                var nam = document.createElement('span');
                nam.innerHTML = name;
                nam.className='nam';
                nm.appendChild(nam);
                nm.appendChild(document.createTextNode(':'+cbox.value));
                var cs = document.getElementById(stt[1]+'cs');
                if(cs===null){
                    cs = document.createElement('div');
                    cs.id = stt[1] + 'cs';
                    cs.className = 'chatspace';
                } 
                cs.appendChild(nm); 
                cs.appendChild(document.createElement('br'));
                val = cbox.value;
                var msg = {"sender":name,"receiver":stt[1],"msg":val,"disc":ch};
                ws.send(JSON.stringify(msg));  
                cbox.value = '';
                cbox.placeholder = 'Press Enter to send';
            }
        }); 
        var bt = document.getElementById(stt[1]+'bt');
        if(bt===null){
            bt = document.createElement("input");
            bt.value = "Send";
            bt.type = 'submit';
            bt.id = stt[1]+'bt';
        }
        bt.onclick = function(){
            var nm = document.createElement('span');
            var nam = document.createElement('span');
            nam.innerHTML = name;
            nam.className='nam';
            nm.appendChild(nam);
            nm.appendChild(document.createTextNode(':'+cbox.value));
            var cs = document.getElementById(stt[1]+'cs');
            if(cs===null){
                cs = document.createElement('div');
                cs.id = stt[1] + 'cs';
                cs.className = 'chatspace';
            } 
            cs.appendChild(nm); 
            cs.appendChild(document.createElement('br'));    
            val = cbox.value;
            var msg = {"sender":name,"receiver":stt[1],"msg":val,"disc":ch};
            ws.send(JSON.stringify(msg)); 
            cbox.value='';
            cbox.placeholder = 'Press Enter to Send';
        };
        d.appendChild(cs);
        d.appendChild(cbox);
        d.appendChild(bt);
        document.body.insertBefore(d,document.getElementById('ifr'));
       }
    }
}
function chatoff(e){
    var dv = document.getElementById(ch+'dv');
    document.getElementById(ch).removeChild(dv);
    var a = e.target;
    var d = document.getElementById(a.id+'d');
    if(d===null){
        d = document.createElement('div');
        d.id = a.id + 'd'; 
        d.className = 'w3-card-2 hats';
    }
    var closebtn = document.getElementById(a.id+'x');
    if(closebtn===null){
        closebtn = document.createElement('button');
        closebtn.innerHTML = 'X';
        closebtn.id = a.id+'x';
    }
    closebtn.onclick = function(e){
        document.getElementById(ch).removeChild(e.target.parentNode);
    };
    d.appendChild(closebtn);
    var ta = document.getElementById(a.id+'ta');
    if(ta===null){
        ta = document.createElement('textarea');
        ta.id = a.id + 'ta';
        ta.placeholder = 'Compose message';
    }
    var bt = document.getElementById(a.id+'bt');
    if(bt===null){
        bt = document.createElement("input");
        bt.value = "Send";
        bt.type = 'submit';
        bt.id = a.id + 'bt';
    }
    ta.focus();
    d.appendChild(ta);
    d.appendChild(bt);
    bt.addEventListener("click",function(e){
        var v = ta.value;
        s.open('post','/Trending/welcome/off');
        s.setRequestHeader("content-type","application/x-www-form-urlencoded");
        s.send("dd="+a.id+"&v="+v+"&n="+name);
        s.onreadystatechange = function(){
            document.getElementById(ch).removeChild(d);
            alert('Message sent successfully.');
        };
        e.preventDefault();
    });
    document.getElementById(ch).insertBefore(d,document.getElementById(ch+'desc'));
    e.preventDefault();
}
function getMsg(e){
    var m = document.getElementById("msg");
    if(m.className.indexOf('w3-show')===-1){
        s.open("get","/Trending/welcome/getMsg");
        s.send(null);
        s.onreadystatechange = function(){
            if(s.readyState===4){
                var st = s.responseXML;
                m.innerHTML="";
                m.className += ' w3-show';
                var a = st.querySelectorAll('a');
                for(i=0;i<a.length;i++){
                    var nname = a[i].querySelector('name').innerHTML.toString();
                    var msg = a[i].querySelector('msg').innerHTML.toString();
                    var nm = document.createElement("span");
                    var ms = document.createElement('p');
                    var dd = document.createElement('div');
                    dd.className = 'dd';
                    dd.id = a[i].querySelector('id').innerHTML;
                    if(a[i].querySelector('flag').innerHTML==='false'){
                        dd.className += ' unread';
                    }
                    nm.innerHTML = nname;
                    if(msg.length>=100){
                        var nmsg = msg.slice(0,96)+'...';
                        ms.innerHTML = nmsg;
                    }else
                        ms.innerHTML = msg;
                    dd.appendChild(nm);
                    dd.appendChild(ms);
                    dd.onclick = function(e){
                        var ddd = e.target;alert(dd.innerHTML);
                        ddd.parentNode.className = ddd.parentNode.className.replace('w3-show','');
                        var showdiv = document.createElement('div');
                        var closebtn = document.createElement('button');
                        var sender = document.createElement('p');
                        var mss = document.createElement('p');
                        var amsg = document.getElementById('amsg');
                        var aspan = amsg.lastElementChild;
                        closebtn.innerHTML = 'X';
                        closebtn.onclick = function(e){
                            document.body.removeChild(e.target.parentNode);
                        };
                        sender.innerHTML = '<i>Sender:</i> '+ddd.querySelector('span').innerHTML;
                        mss.innerHTML = '<i>Message:</i> '+ddd.querySelector('p').innerHTML;
                        showdiv.className = 'showdiv';
                        showdiv.appendChild(closebtn);
                        showdiv.appendChild(sender);
                        showdiv.appendChild(mss);
                        if(ddd.className.indexOf('unread')!==-1){
                            ddd.className = ddd.className.replace('unread','');
                            aspan.innerHTML = Number(aspan.innerHTML) - 1;
                            if(aspan.innerHTML===0)aspan.innerHTML = '';
                            var st = createXHR();
                            st.open("get","/Trending/welcome/read/"+ddd.id);
                            st.send(null);
                        }
                        document.body.insertBefore(showdiv,document.getElementById('ifr'));
                    };
                    dd.appendChild(document.createElement('hr'));
                    m.appendChild(dd);
                }
            }
        };
        e.preventDefault();
    }else{
        m.className = m.className.replace('w3-show','');
        e.preventDefault();
    }
}
function manageAcct(e){
    var acctdiv = document.getElementById('acct');
    if(acctdiv.className.indexOf('w3-show')===-1){
        acctdiv.innerHTML = '';
        acctdiv.className += ' w3-show'; 
        var deleteacct = document.createElement('a');
        deleteacct.href = '';
        deleteacct.innerHTML = 'Delete Account';
        deleteacct.onclick = function(){
            s.open("get","/Trending/welcome/delete");
            s.send(null);
            s.onreadystatechange = function(){
                if(s.readyState===4){
                    if(s.responseText==='true')
                        location.href = 'index.html';
                }
            };
        };
        var macct = document.createElement('a');
        macct.href='';
        macct.innerHTML = 'Manage Account';
        macct.onclick = function(){
            var acdiv = document.createElement('div');
            var aepic = document.createElement('p');
            aepic.innerHTML = '<span>Add/Edit Picture</span><input '
        };
        acctdiv.appendChild(macct);
        var mtopics = document.createElement('a');
        mtopics.href = '';
        mtopics.innerHTML = 'Manage Topics';
        mtopics.onclick = function(e){
            s.open("get","/Trending/welcome/mtopics");
            s.send(null);
            s.onreadystatechange = function(){
                if(s.readyState===4){
                    acctdiv.innerHTML = '';
                    var st = s.responseXML;
                    var t = st.querySelectorAll('topic');
                    for(j=0;j<t.length;j++){
                        var divv = document.createElement('div');
                        divv.className = 'ddiv';
                        var span = document.createElement('span');
                        span.innerHTML = t[j].innerHTML;
                        var dele = document.createElement('button');
                        dele.innerHTML = 'Delete Topic';
                        dele.id = 'o'+t[j].innerHTML;
                        dele.onclick = function(ev){
                            s.open("get","/Trending/welcome/dtopic/"+ev.target.id.slice(1));
                            s.send(null);
                            s.onreadystatechange = function(){
                                if(s.readyState===4){
                                    acctdiv.className = acctdiv.className.replace('w3-show','');
                                    alert('Discussion deleted.');
                                    ul.removeChild(document.getElementById(ev.target.id.slice(1)));
                                }
                        };
                        };
                        divv.appendChild(span);
                        divv.appendChild(dele);
                        acctdiv.appendChild(divv);
                    }
                }
            };
            e.preventDefault();
        };
        acctdiv.appendChild(mtopics);
        acctdiv.appendChild(deleteacct);
        e.preventDefault();
    }else{
        acctdiv.className = acctdiv.className.replace('w3-show','');
        e.preventDefault();
    }
}