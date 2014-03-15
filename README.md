# Web site visit statictics

This app can record page visit,get visit count for page,for category and for site.

Usage:
```
首页的计数，用下面的代码：
<script type="text/javascript">
var src = "http://vr.fh.gov.cn?siteid=" + "uuid-here" + "&urlt=home";
var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>
如果放在页面的最末端的话，也可以写成：
<script type="text/javascript" src="http://vr.fh.gov.cn?siteid=uuid&urlt=home"></script>


文章页面的计数，如果您不想查询热门文章，可以忽略title字段。如果不需要显示访问次数，也可以去掉callback。
<script type="text/javascript">
var vr_callback = function(count){
    document.getElementById("vr_count").innerHTML = count;
};
var title = encodeURIComponent('市妇联与亚德客再度牵手  “寒冬送暖善行人间齐心协力共建奉城”为主题的“2014阳光行动”圆满落幕');
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" +
    "&urlt=detail&title=" + title + "&callback=vr_callback";
var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>

列表页面的计数，如果不需要查询热门栏目，可以不用title字段，如果不需要访问次数，也可以去掉callback。

<script type="text/javascript">
var vr_callback = function(count){
    document.getElementById("vr_count").innerHTML = count;
};
var title = encodeURIComponent('目录名称');
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" + "&catid=" + catid +
    "&urlt=list&title=" + title + "&callback=vr_callback";
var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>

显示整站访问次数，注意out字段，该请求不产生访问记录。
<script type="text/javascript">
var sitecount_callback = function(count){
    document.getElementById("wholesite_count").innerHTML = count;
};
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" +
        "&out=sitecount&callback=sitecount_callback";

var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>

显示栏目列表页访问次数，注意out字段，该请求不产生访问记录。
<script type="text/javascript">
var sitecount_callback = function(count){
    document.getElementById("wholesite_count").innerHTML = count;
};
var src = "http://vr.fh.gov.cn?siteid=" + "c9f36d3e-00a4-4139-882c-022c8034f58d" + "&catid=" + catid +
        "&out=catcount&callback=sitecount_callback";

var s = '<' + 'script ' + 'src="' + src + '"' + '><' + '/script>';
document.write(s);
</script>

```

用来统计站点的文章访问数，同时记录详细的客户端信息，包括ip，ua。引入的数据可以在后台通过mongodb的MapReduce或者hadoop分析处理。
