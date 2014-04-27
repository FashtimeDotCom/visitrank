//mapfunc:
function mapFunction() {
    var key = this.url, value = {
        url : this.url,
        count : 1
    };
    emit(key, value);
}

//reducefunc:
function reduceFunction(key, values) {
    var reducedObject = {
        url : key,
        count : 0
    };

    values.forEach(function(value) {
        reducedObject.count += value.count;
    });
    return reducedObject;
}
//finalizefunc: