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
    if (hostAndURI) {
        hostAndURI = hostAndURI.trim()
    }
    var sendParams = {
        "pageno": page,
        "hostAndURI": hostAndURI
    }
    $.ajax({
        type: "GET",
        url: url,
        data: sendParams,
        dataType: "json",
        success: function (msg) {
            var pageno = msg.pageno;
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


function loadData(jsonArray) {
    console.log(jsonArray);
    var objs = jsonArray.data;
    var html = '';

    for (var i = 0; i < objs.length; i++) { //循环后台传过来的Json数组
        var obj = objs[i];
        var clientReqUrl = obj.clientReqUrl;
        var destServerUrl = obj.destServerUrl;
        var enable = obj.enable;
            var trs = '<tr>  '
                + ' <td class="hidden-480" ><textarea name="clientReqUrl">' + clientReqUrl + '</textarea></td> '
                + ' <td class="hidden-480"><textarea name="destServerUrl">' + destServerUrl + '</textarea></td>'
                + ' <td class="hidden-480" ><textarea name="enable">' + enable + '</textarea></td> '
                + ' <th class="hidden-480" onclick="saveOrUpdateProxy(this.parentNode)" ><button>' + "确定修改" + '</button></th> '
                + ' </tr> ';

            html += trs;
    }
    $("#data").html(html);
}

function show(obj) {
    //console.log(obj);
    $("#show_button").click();
    $("#show_data").text($(obj).text());
}

// function deleteProxy(obj) {
//     $(obj).remove();
// }

function showAdd(obj) {
    //console.log(obj);
    $("#show_button2").click();
}

function saveOrUpdateProxy(obj) {
    var clientReqUrl;
    var destServerUrl;
    var enable;
    $(obj).children('td').each(function (j) { //遍历 tr 的各个 td
        console.log(this.childNodes[0].name);
        switch (this.childNodes[0].name) {
            case "clientReqUrl":
                console.log(this.childNodes[0].value);
                clientReqUrl = this.childNodes[0].value;
                break;
            case "destServerUrl":
                destServerUrl = this.childNodes[0].value;
                break;
            case "enable":
                enable = this.childNodes[0].value;
                break;
        }
    });
    var send = {
        "clientReqUrl": clientReqUrl,
        "destServerUrl": destServerUrl,
        "enable": enable,
    }
    console.log(clientReqUrl);

    $.ajax({
        type: "post",
        url: url_update,
        data: send,
        dataType: "json",
        success: function (msg) {
            console.log(msg);
            alert(msg.success==1?"修改成功":"失败")
        }
    });
}

function addContent(obj) {
    console.log(obj);

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
