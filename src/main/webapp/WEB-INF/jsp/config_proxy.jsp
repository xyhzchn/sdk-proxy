<%@ page import="com.cn.ceshi.model.Log2DB" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: wangly
  Date: 2017/9/22
  Time: 14:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>日志代理</title>
    <meta name="keywords" content="Bootstrap模版,Bootstrap模版下载,Bootstrap教程,Bootstrap中文"/>
    <meta name="description" content="金林苑提供Bootstrap模版,Bootstrap教程,Bootstrap中文翻译等相关Bootstrap插件下载"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <!-- basic styles -->

    <link href="<%=basePath%>AceAdmin/assets/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="<%=basePath%>AceAdmin/assets/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="<%=basePath%>AceAdmin/assets/css/ace.min.css"/>
    <link rel="stylesheet" href="<%=basePath%>AceAdmin/assets/css/ace-rtl.min.css"/>
    <link rel="stylesheet" href="<%=basePath%>AceAdmin/assets/css/ace-skins.min.css"/>

    <!--[if lte IE 8]>
    <link rel="stylesheet" href="<%=basePath%>AceAdmin/assets/css/ace-ie.min.css"/>
    <![endif]-->
    <script src="<%=basePath%>AceAdmin/assets/js/ace-extra.min.js"></script>
    <!--[if lt IE 9]>
    <script src="<%=basePath%>AceAdmin/assets/js/html5shiv.js"></script>
    <script src="<%=basePath%>AceAdmin/assets/js/respond.min.js"></script>
    <![endif]-->
</head>
<style type="text/css">
    .none-td {

    }

    .upgrdeNew {

    }

    .upgrdeNew table {
        table-layout: fixed; /* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
    }

    .upgrdeNew td {
        width: 100%;
        word-break: keep-all; /* 不换行 */
        white-space: nowrap; /* 不换行 */
        overflow: hidden; /* 内容超出宽度时隐藏超出部分的内容 */
        text-overflow: ellipsis; /* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
    }


</style>
<body>
<div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try {
            ace.settings.check('navbar', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="navbar-container" id="navbar-container">
        <div class="navbar-header pull-left">
            <a href="#" class="navbar-brand">
                <small>
                    <i class="icon-leaf"></i>
                    fidder代理日志管理
                </small>
            </a><!-- /.brand -->
        </div><!-- /.navbar-header -->

        <div class="navbar-header pull-right" role="navigation">

            <ul class="nav ace-nav">
                <li class="grey">
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                        <i class="icon-tasks"></i>
                        <span class="badge badge-grey">4</span>
                    </a>

                    <ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-close">
                        <li class="dropdown-header">
                            <i class="icon-ok"></i>
                            4 Tasks to complete
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
                                    <span class="pull-left">Software Update</span>
                                    <span class="pull-right">65%</span>
                                </div>

                                <div class="progress progress-mini ">
                                    <div style="width:65%" class="progress-bar "></div>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
                                    <span class="pull-left">Hardware Upgrade</span>
                                    <span class="pull-right">35%</span>
                                </div>

                                <div class="progress progress-mini ">
                                    <div style="width:35%" class="progress-bar progress-bar-danger"></div>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
                                    <span class="pull-left">Unit Testing</span>
                                    <span class="pull-right">15%</span>
                                </div>

                                <div class="progress progress-mini ">
                                    <div style="width:15%" class="progress-bar progress-bar-warning"></div>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
                                    <span class="pull-left">Bug Fixes</span>
                                    <span class="pull-right">90%</span>
                                </div>

                                <div class="progress progress-mini progress-striped active">
                                    <div style="width:90%" class="progress-bar progress-bar-success"></div>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                See tasks with details
                                <i class="icon-arrow-right"></i>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="purple">
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                        <i class="icon-bell-alt icon-animated-bell"></i>
                        <span class="badge badge-important">8</span>
                    </a>

                    <ul class="pull-right dropdown-navbar navbar-pink dropdown-menu dropdown-caret dropdown-close">
                        <li class="dropdown-header">
                            <i class="icon-warning-sign"></i>
                            8 Notifications
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
											<span class="pull-left">
												<i class="btn btn-xs no-hover btn-pink icon-comment"></i>
												New Comments
											</span>
                                    <span class="pull-right badge badge-info">+12</span>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <i class="btn btn-xs btn-primary icon-user"></i>
                                Bob just signed up as an editor ...
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
											<span class="pull-left">
												<i class="btn btn-xs no-hover btn-success icon-shopping-cart"></i>
												New Orders
											</span>
                                    <span class="pull-right badge badge-success">+8</span>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <div class="clearfix">
											<span class="pull-left">
												<i class="btn btn-xs no-hover btn-info icon-twitter"></i>
												Followers
											</span>
                                    <span class="pull-right badge badge-info">+11</span>
                                </div>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                See all notifications
                                <i class="icon-arrow-right"></i>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="green">
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                        <i class="icon-envelope icon-animated-vertical"></i>
                        <span class="badge badge-success">5</span>
                    </a>

                    <ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-close">
                        <li class="dropdown-header">
                            <i class="icon-envelope-alt"></i>
                            5 Messages
                        </li>

                        <li>
                            <a href="#">
                                <img src="<%=basePath%>AceAdmin/assets/avatars/avatar.png" class="msg-photo"
                                     alt="Alex's Avatar"/>
                                <span class="msg-body">
											<span class="msg-title">
												<span class="blue">Alex:</span>
												Ciao sociis natoque penatibus et auctor ...
											</span>

											<span class="msg-time">
												<i class="icon-time"></i>
												<span>a moment ago</span>
											</span>
										</span>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <img src="<%=basePath%>AceAdmin/assets/avatars/avatar3.png" class="msg-photo"
                                     alt="Susan's Avatar"/>
                                <span class="msg-body">
											<span class="msg-title">
												<span class="blue">Susan:</span>
												Vestibulum id ligula porta felis euismod ...
											</span>

											<span class="msg-time">
												<i class="icon-time"></i>
												<span>20 minutes ago</span>
											</span>
										</span>
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <img src="<%=basePath%>AceAdmin/assets/avatars/avatar4.png" class="msg-photo"
                                     alt="Bob's Avatar"/>
                                <span class="msg-body">
											<span class="msg-title">
												<span class="blue">Bob:</span>
												Nullam quis risus eget urna mollis ornare ...
											</span>

											<span class="msg-time">
												<i class="icon-time"></i>
												<span>3:15 pm</span>
											</span>
										</span>
                            </a>
                        </li>

                        <li>
                            <a href="inbox.html">
                                See all messages
                                <i class="icon-arrow-right"></i>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="light-blue">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                        <img class="nav-user-photo" src="<%=basePath%>AceAdmin/assets/avatars/user.jpg"
                             alt="Jason's Photo"/>
                        <span class="user-info">
									<small>Welcome,</small>
									Jason
								</span>

                        <i class="icon-caret-down"></i>
                    </a>

                    <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        <li>
                            <a href="#">
                                <i class="icon-cog"></i>
                                Settings
                            </a>
                        </li>

                        <li>
                            <a href="#">
                                <i class="icon-user"></i>
                                Profile
                            </a>
                        </li>

                        <li class="divider"></li>

                        <li>
                            <a href="#">
                                <i class="icon-off"></i>
                                Logout
                            </a>
                        </li>
                    </ul>
                </li>
            </ul><!-- /.ace-nav -->
        </div><!-- /.navbar-header -->
    </div><!-- /.container -->
</div>

<div class="main-container" id="main-container">
    <script type="text/javascript">
        try {
            ace.settings.check('main-container', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="main-container-inner">
        <a class="menu-toggler" id="menu-toggler" href="#">
            <span class="menu-text"></span>
        </a>

        <div class="sidebar" id="sidebar">
            <script type="text/javascript">
                try {
                    ace.settings.check('sidebar', 'fixed')
                } catch (e) {
                }
            </script>

            <div class="sidebar-shortcuts" id="sidebar-shortcuts">
                <div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
                    <button class="btn btn-success">
                        <i class="icon-signal"></i>
                    </button>

                    <button class="btn btn-info">
                        <i class="icon-pencil"></i>
                    </button>

                    <button class="btn btn-warning">
                        <i class="icon-group"></i>
                    </button>

                    <button class="btn btn-danger">
                        <i class="icon-cogs"></i>
                    </button>
                </div>

                <div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
                    <span class="btn btn-success"></span>

                    <span class="btn btn-info"></span>

                    <span class="btn btn-warning"></span>

                    <span class="btn btn-danger"></span>
                </div>
            </div><!-- #sidebar-shortcuts -->
            <ul class="nav nav-list">


                <li>
                    <a href="#" class="dropdown-toggle">
                        <i class="icon-tag"></i>
                        <span class="menu-text"> 更多页面 </span>

                        <b class="arrow icon-angle-down"></b>
                    </a>

                    <ul class="submenu" style="display: block;">
                        <li>
                            <a href="/admin/page">
                                <i class="icon-double-angle-right"></i>
                                查询日志列表
                            </a>
                        </li>
                        <li>
                            <a href="/admin/page/config">
                                <i class="icon-double-angle-right"></i>
                                配置自定义参数
                            </a>
                        </li>
                        <li>
                            <a href="/admin/page/config_rsa">
                                <i class="icon-double-angle-right"></i>
                                配置接口RSA密钥
                            </a>
                        </li>
                        <li>
                            <a href="/admin/page/config_proxy">
                                <i class="icon-double-angle-right"></i>
                                客户端接口转发设置
                            </a>
                        </li>
                    </ul>
                </li>


            </ul><!-- /.nav-list -->

            <div class="sidebar-collapse" id="sidebar-collapse">
                <i class="icon-double-angle-left" data-icon1="icon-double-angle-left"
                   data-icon2="icon-double-angle-right"></i>
            </div>

            <script type="text/javascript">
                try {
                    ace.settings.check('sidebar', 'collapsed')
                } catch (e) {
                }
            </script>
        </div>

        <div class="main-content">
            <div class="breadcrumbs" id="breadcrumbs">
                <script type="text/javascript">
                    try {
                        ace.settings.check('breadcrumbs', 'fixed')
                    } catch (e) {
                    }
                </script>

                <ul class="breadcrumb">
                    <li>
                        <i class="icon-home home-icon"></i>
                    </li>
                </ul><!-- .breadcrumb -->
            </div>
            <div class="page-content">
                <div class="page-header">
                    <h1>
                        列表
                        <small>
                            <i class="icon-double-angle-right"></i>
                            日志 &amp;列表
                        </small>
                    </h1>
                </div><!-- /.page-header -->
                <div class="row" style="width: 600px;">

                    <div class='col-sm-4'>
                        <div class="form-group">
                            <label> 接口地址：</label>
                            <!--指定 date标记-->
                            <div class='input-group'>
                                <input type='text' id="hostAndURI" class="form-control"/>

                            </div>
                        </div>
                    </div>
                    <div class='col-sm-2'>
                        <div class="form-group">
                            <label>&nbsp;</label>
                            <!--指定 date标记-->
                            <div class='input-group'>
                                <input type="button" onclick="paging(1)" value="查询"/>
                            </div>
                        </div>
                    </div>
                    <div class='col-sm-2'>
                        <div class="form-group">
                            <label>&nbsp;</label>
                            <!--指定 date标记-->
                            <div class='input-group'>
                                <input type="button" onclick="showAdd()" value="增加"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12">
                        <!-- PAGE CONTENT BEGINS -->

                        <div class="row">
                            <div class="col-xs-12">
                                <div class="table-responsive upgrdeNew">
                                    <table id="sample-table-1"
                                           class="table table-striped table-bordered table-hover">
                                        <thead>
                                        <tr>
                                            <th>接口地址</th>
                                            <th>目标服务器接口地址</th>
                                            <th>是否启用</th>
                                            <th colspan="2">操作</th>
                                        </tr>
                                        </thead>
                                        <tbody id="data">
                                        </tbody>

                                    </table>
                                </div><!-- /.table-responsive -->
                            </div><!-- /span -->
                        </div><!-- /row -->

                        <div class="hr hr-18 dotted hr-double"></div>
                        <div class="row">
                            <ul class="pagination" id="pageUl">

                            </ul>
                        </div>
                        <h4 class="pink" style="display: none">
                            <i class="icon-hand-right icon-animated-hand-pointer blue"></i>
                            <a id="show_button" href="#modal-table-x" role="button" class="green" data-toggle="modal">
                                Table Inside a Modal
                                Box </a>
                        </h4>

                        <div class="hr hr-18 dotted hr-double"></div>


                        <div id="modal-table-x" class="modal fade" tabindex="-1">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header no-padding">
                                        <div class="table-header">
                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                                <span class="white">&times;</span>
                                            </button>
                                            详细数据
                                        </div>
                                    </div>

                                    <div class="modal-body no-padding" style="height: 200px;">
                                        <textarea id="show_data" style="width: 578px;height: inherit;">

                                        </textarea>
                                    </div>

                                    <div class="modal-footer no-margin-top">
                                        <button class="btn btn-sm btn-danger pull-left" data-dismiss="modal">
                                            <i class="icon-remove"></i>
                                            Close
                                        </button>
                                    </div>
                                </div><!-- /.modal-content -->
                            </div><!-- /.modal-dialog -->
                        </div><!-- PAGE CONTENT ENDS -->

                        <div id="modal-table-x2" class="modal fade" tabindex="-1" >
                            <div class="modal-dialog">
                                <div class="modal-content" style="width: 1050px;">
                                    <div class="modal-header no-padding">
                                        <div class="table-header">
                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                                <span class="white">&times;</span>
                                            </button>
                                            详细数据
                                        </div>
                                    </div>

                                    <div class="modal-body no-padding" style="height: 200px;">
                                        <table
                                               class="table table-striped table-bordered table-hover">
                                            <thead>
                                            <tr>
                                                <th>接口地址</th>
                                                <th>目标服务器接口地址</th>
                                                <th>是否启用</th>
                                            </tr>
                                            </thead>
                                            <tbody id="data2">
                                            <tr>
                                                <td class="hidden-480"><textarea name="clientReqUrl"></textarea> </td>
                                                <td class="hidden-480"><textarea name="destServerUrl"></textarea></td>
                                                <td class="hidden-480"><textarea name="enable"></textarea></td>
                                                <th class="hidden-480" onclick="saveOrUpdateProxy(this.parentNode)">
                                                    <button>确定修改</button>
                                                </th>
                                            </tr>
                                            </tbody>

                                        </table>
                                    </div>

                                    <div class="modal-footer no-margin-top">
                                        <button class="btn btn-sm btn-danger pull-left" data-dismiss="modal">
                                            <i class="icon-remove"></i>
                                            Close
                                        </button>
                                    </div>
                                </div><!-- /.modal-content -->
                            </div><!-- /.modal-dialog -->
                        </div><!-- PAGE CONTENT ENDS -->
                        <h4 class="pink" style="display: none">
                            <i class="icon-hand-right icon-animated-hand-pointer blue"></i>
                            <a id="show_button2" href="#modal-table-x2" role="button" class="green" data-toggle="modal">
                                Table Inside a Modal
                                Box </a>
                        </h4>

                    </div><!-- /.col -->

                </div><!-- /.row -->


            </div><!-- /.page-content -->
        </div><!-- /.main-content -->


    </div><!-- /.main-container-inner -->


    <a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
        <i class="icon-double-angle-up icon-only bigger-110"></i>
    </a>
</div>


<script type="text/javascript">
    window.jQuery || document.write("<script src='<%=basePath%>AceAdmin/assets/js/jquery-1.10.2.min.js'>" + "<" + "/script>");
</script>
<script type="text/javascript">
    if ("ontouchend" in document) document.write("<script src='<%=basePath%>assets/js/jquery.mobile.custom.min.js'>" + "<" + "/script>");
</script>
<script src="<%=basePath%>AceAdmin/assets/js/bootstrap.min.js"></script>
<script src="<%=basePath%>AceAdmin/assets/js/typeahead-bs2.min.js"></script>

<!-- page specific plugin scripts -->

<script src="<%=basePath%>AceAdmin/assets/js/jquery.dataTables.min.js"></script>
<script src="<%=basePath%>AceAdmin/assets/js/jquery.dataTables.bootstrap.js"></script>

<!-- ace scripts -->

<script src="<%=basePath%>AceAdmin/assets/js/ace-elements.min.js"></script>
<script src="<%=basePath%>AceAdmin/assets/js/ace.min.js"></script>
<script src="<%=basePath%>AceAdmin/page/bootstrap-paginator.js"></script>

<!-- inline scripts related to this page -->
<link href="<%=basePath%>AceAdmin/date/css/bootstrap-datetimepicker.min.css" rel="stylesheet"/>
<script src="<%=basePath%>AceAdmin/date/js/moment-with-locales.min.js"></script>
<script src="<%=basePath%>AceAdmin/date/js/bootstrap-datetimepicker.min.js"></script>
<script>
    var url = "<%=basePath%>/admin/config/query_proxy";
    var url_update = "<%=basePath%>/admin/config/update_proxy";

</script>
<script src="<%=basePath%>AceAdmin/doing_proxy.js"></script>


</body>
</html>

