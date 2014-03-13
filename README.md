# Web site visit statictics

This app can record page visit record,get page visit count for page,for category and for site.

It's very fast. on my local machine test,can handle 5000+ request per second. If add rabbitmq,it will even more fast.

Usage:
```
put code below in your page,for detail page urlt=detail, for list page,urlt=list,and urlt=home for homepage.
for detail page:
<script src="http://vr.fh.gov.cn?siteid=uuid&ot=documentwrite&urlt=detail&title=xxxx"></script>

for list page:
<script src="http://vr.fh.gov.cn?siteid=uuid&out=none&urlt=list&title=xxxx"></script>

for home page:
<script src="http://vr.fh.gov.cn?siteid=uuid&out=none&urlt=home"></script>

get site top visited article pages:
<script>
function callback(sitehotestarticles){

}
</script>
<script src="http://vr.fh.gov.cn?siteid=uuid&out=sitehotest&callback=callback"></script>

```

用来统计站点的文章访问数，同时记录详细的客户端信息，包括ip，ua。它非常快，在我本机测试中可以承受5000+，由于mongodb的锁的关系，如果引入rabbitmq的话，会更加快速。引入的数据可以在后台通过mongodb的MapReduce或者hadoop分析处理。
