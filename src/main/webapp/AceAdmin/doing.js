$(function () {
    $('#datetimepicker1').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: moment.locale('zh-cn')
    });
    $('#datetimepicker2').datetimepicker({
        format: 'YYYY-MM-DD hh:mm',
        locale: moment.locale('zh-cn')
    });
});

jQuery(function ($) {
    paging(1);
    var oTable1 = $('#sample-table-2').dataTable({
        "aoColumns": [
            {"bSortable": false},
            null, null, null, null, null,
            {"bSortable": false}
        ]
    });


    $('table th input:checkbox').on('click', function () {
        var that = this;
        $(this).closest('table').find('tr > td:first-child input:checkbox')
            .each(function () {
                this.checked = that.checked;
                $(this).closest('tr').toggleClass('selected');
            });

    });


    $('[data-rel="tooltip"]').tooltip({placement: tooltip_placement});
    function tooltip_placement(context, source) {
        var $source = $(source);
        var $parent = $source.closest('table')
        var off1 = $parent.offset();
        var w1 = $parent.width();

        var off2 = $source.offset();
        var w2 = $source.width();

        if (parseInt(off2.left) < parseInt(off1.left) + parseInt(w1 / 2)) return 'right';
        return 'left';
    }
})


function paging(page) {
    var hostAndURI = $("#hostAndURI").val();
    var createAt = $("#createAt").val();
    if(hostAndURI){
        hostAndURI=hostAndURI.trim()
    }
    var sendParams = {
        "pageno": page,
        "hostAndURI": hostAndURI,
        "createAt": createAt,
    }
    $.ajax({
        type: "GET",
        url: url,
        data: sendParams,
        dataType: "json",
        success: function (msg) {
            var pageno = msg.pageno;
            //console.log(pageno);
            var pagesize = msg.pagesize;
            var total = msg.total;
            $("#data").html("");
            if (total == 0) {
                $('#pageUl').html("");
                return;
            }
            loadData(msg);
            var element = $('#pageUl');//对应下面ul的ID
            var options = {
                bootstrapMajorVersion: 3,
                currentPage: pageno,//当前页面
                numberOfPages: 6,//一页显示几个按钮（在ul里面生成5个li）
                totalPages: total, //总页数
                onPageClicked: function (event, originalEvent, typePage, currentPage) {
                    paging(currentPage);
                }
            }
            element.bootstrapPaginator(options);
        }
    });

}

function clearHistory(obj) {
    if(!confirm("你确定要清空数据么？")) {
        return;
    }
    var hostAndURI = $("#hostAndURI").val();
    var createAt = $("#createAt").val();
    if(hostAndURI){
        hostAndURI=hostAndURI.trim()
    }
    var sendParams = {
        "hostAndURI": hostAndURI,
        "createAt": createAt,
    }
    $.ajax({
        type: "GET",
        url: url_clear,
        data: sendParams,
        dataType: "json",
        success: function (msg) {
            var pageno = msg.pageno;
            var pagesize = msg.pagesize;
            var total = msg.total;
            $("#data").html("");
            if (total == 0) {
                $('#pageUl').html("");
                alert(msg.success==1?"清除成功":"清除失败")
                return;
            }
            loadData(msg);
            var element = $('#pageUl');//对应下面ul的IDx
            var options = {
                bootstrapMajorVersion: 3,
                currentPage: pageno,//当前页面
                numberOfPages: 6,//一页显示几个按钮（在ul里面生成5个li）
                totalPages: total, //总页数
                onPageClicked: function (event, originalEvent, typePage, currentPage) {
                    paging(currentPage);
                }
            }
            element.bootstrapPaginator(options);
        }
    });
}

function loadData(jsonArray) {
    var html = '';
    var objs = jsonArray.data;
    for (var i = 0; i < objs.length; i++) { //循环后台传过来的Json数组
        var obj = objs[i];
       // var send = JSON.parse(obj.sendParams);
        var send = eval("("+obj.sendParams+")");
        //var receive = JSON.parse(obj.backData);
        var receive = eval("("+obj.backData+")");
        //console.log(send)
        var passDesMsg = send.passDes == -1 ? '参数无需解密' : send.passDes == 0 ? '参数解密失败' :send.passDes == -2 ?'不需要解密或者解密错误': '解密成功';
        var trs = '<tr>  '
            + ' <td class="hidden-480 none-td" onclick="show(this)"> <a href="#"> ' + obj.hostAndURI + '</a></td>'
            + '<td >' + (obj.code == 1 ? 'success' : 'fail') + '</td>'
            + '<td class="hidden-480"> ' + passDesMsg + '</td>';

        if (send.passDes == 1) {
            if (send.success == 1) {
                trs += '<td style="color: green">' + "无误" + '</td>'
            } else {
                trs += '<td style="color: orange" onclick="show(this)">' + send.errorMsg + '</td>'
            }
        } else {
            trs += '<td >' + passDesMsg + '</td>'
        }
        var showParam;
        if(typeof(send.resultData)   !=   "object"){
           showParam=send.resultData;
        }else{
            showParam=JSON.stringify(send.resultData);
        }
        var showReveiceParam="";
        if(typeof(receive.resultData)   !=   "object"){
            showReveiceParam=receive.resultData;
        }else{
            showReveiceParam=JSON.stringify(receive.resultData);
        }

        trs += ' <td class="hidden-480" onclick="show(this)">' + showParam + '</td> '
            + ' <td class="hidden-480" onclick="show(this)">' + obj.reqOriginParam + '</td> '
            + '<td class="hidden-480" onclick="show(this)">' + showReveiceParam + '</td>'
            + ' <td class="hidden-480" onclick="show(this)">' + obj.respOriginParam + '</td> '
            + '<td class="hidden-480"> ' + long2string(obj.createAt) + '</td> '
            + '<td>' + long2string(obj.requestTime) + '</td>'
            + '<td>' + long2string(obj.responseTime) + '</td>'
            + '</tr> ';
        html += trs;
    }
    $("#data").html(html);
}

function show(obj) {
    //console.log(obj);
    $("#show_button").click();
    $("#show_data").text($(obj).text());
}
function long2string(obj) {
    var d = new Date();
    d.setTime(obj);
    var s = d.format('yyyy-MM-dd HH:mm:ss');
    return s;
}
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    var week = {
        "0": "\u65e5",
        "1": "\u4e00",
        "2": "\u4e8c",
        "3": "\u4e09",
        "4": "\u56db",
        "5": "\u4e94",
        "6": "\u516d"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "\u661f\u671f" : "\u5468") : "") + week[this.getDay() + ""]);
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}
