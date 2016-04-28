<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="pages" uri="http://ququzone.github.com/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../include/head.jsp"/>
    <title>用户管理</title>
</head>
<body class="nav-md">

<div class="container body">
    <div class="main_container">
        <jsp:include page="/nav"/>

        <div class="right_col" role="main">
            <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>用户列表</h2>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <div class="pull-right">
                                <button id="btn-add-modal" class="btn btn-success"><span
                                        class="fa fa-plus-circle"></span> 新增
                                </button>
                            </div>
                            <table class="table table-striped table-bordered dataTable no-footer">
                                <thead>
                                <tr>
                                    <th>用户名</th>
                                    <th>姓名</th>
                                    <th>状态</th>
                                    <th>加入时间</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="user" items="${users.data}">
                                    <tr>
                                        <td>${user.username}</td>
                                        <td>${user.displayName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${user.status == 'NORMAL'}">正常</c:when>
                                                <c:when test="${user.status == 'DISABLE'}">冻结</c:when>
                                                <c:otherwise>未知</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><fmt:formatDate value="${user.createdTime}"
                                                            pattern="yyyy-MM-dd HH:mm"/></td>
                                        <td>
                                            <button class="btn btn-sm btn-primary"><span class="fa fa-edit"></span> 编辑
                                            </button>
                                            <c:if test="${user.status == 'NORMAL'}">
                                                <button class="btn btn-sm btn-danger"><span class="fa fa-lock"></span>
                                                    禁用
                                                </button>
                                                <button class="btn btn-sm btn-info"><span
                                                        class="fa fa-users"></span>
                                                    分配角色
                                                </button>
                                                <button class="btn btn-sm btn-danger"><span
                                                        class="fa fa-refresh"></span>
                                                    重置密码
                                                </button>
                                            </c:if>
                                            <c:if test="${user.status == 'DISABLE'}">
                                                <button class="btn btn-sm btn-info"><span class="fa fa-unlock"></span>
                                                    激活
                                                </button>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                            <pages:p current="${users.current}" totalPage="${users.totalPage}"
                                     baseUrl="/users"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../include/footer.jsp"/>
    </div>
</div>
<div class="modal fade" id="modal-add" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">新增用户</h4>
            </div>
            <div class="modal-body">
                <form id="form-add" class="form-horizontal form-label-left">
                    <div class="form-group">
                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="username">用户名
                            <span class="required">*</span>
                        </label>
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <input name="username" type="text" id="username" required="required"
                                   class="form-control col-md-7 col-xs-12">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="display-name">姓名
                            <span class="required">*</span>
                        </label>
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <input name="display_name" type="text" id="display-name" required="required"
                                   class="form-control col-md-7 col-xs-12">
                        </div>
                    </div>
                    <div class="form-group password-area">
                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="password">密码
                            <span class="required">*</span>
                        </label>
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <input type="password" id="password" name="password" required="required"
                                   class="form-control col-md-7 col-xs-12 input-password">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button id="btn-add-submit" type="button" class="btn btn-primary"><span
                        class="fa fa-plus-circle"></span> 新增
                </button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../include/script.jsp"/>
<script type="text/javascript" src="/resources/js/icheck/icheck.min.js"></script>
<script type="text/javascript" src="/resources/js/parsley/parsley.min.js"></script>
<script type="text/javascript" src="/resources/js/parsley/zh_cn.js"></script>
<script type="application/javascript">
    $(document).ready(function () {
        $('#btn-add-modal').click(function () {
            $('#modal-add').modal('toggle')
        });
        $('#btn-add-submit').click(function (e) {
            e.preventDefault();
            $('#form-add').parsley().validate();
            if ($('#form-add').parsley().isValid()) {
                $.ajax({
                    url: '/user',
                    method: 'POST',
                    dataType: 'json',
                    data: $('#form-add').serialize(),
                    success: function () {
                        window.location.reload();
                    },
                    error: function (data) {
                        notie.alert(3, data.responseJSON.error, 2.5);
                    }
                });
            }
        })
    });
</script>
</body>
</html>
