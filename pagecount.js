/*
document.querySelector(selector) — fetches the first matching node only
document.getElementById(idname) — fetches a single node by its ID name
document.getElementsByTagName(tagname) — fetches nodes matching an element (e.g. h1, p, strong, etc).
document.getElementsByClassName(class) — fetches nodes with a specific class name

document.getElementById("container").innerHTML += "<p>more content</p>";
var p = document.createElement("p");
p.appendChild(document.createTextNode("more content");
document.getElementById("container").appendChild(p);

document.getElementById("container").innerHTML = null;

var c = document.getElementById("container");
while (c.lastChild) c.removeChild(c.lastChild);

var c = document.getElementById("container");
c.parentNode.removeChild(c);

encodeURIComponent()
 */

/*
 * 这段代码应用于文章显示的页面，#countid就是输出数字的地方。
 */
<script type="text/javascript">
var vr_callback = function(count){
    document.getElementById("vr_count").innerHTML = count;
};

var title = encodeURIComponent(""); //get title from page.
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" +
        "&urlt=detail&title=" + title  + "&callback=vr_callback";

var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>

/**
 * 这段代码，用来统计首页访问量
 */
<script type="text/javascript">
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" + "&urlt=home";
var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>

/**
 * 这段代码，用来统计整站访问量
 */
<script type="text/javascript">
var vr_callback = function(count){
    document.getElementById("wholesite_count").innerHTML = count;
};
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" +
        "&urlt=detail&out=sitecount&callback=wholesite_count";

var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>