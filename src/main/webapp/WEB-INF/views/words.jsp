<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tag/AuthTag.tld" prefix="z" %>
<html>
<head>
    <title>View words</title>
    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/jquery.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/style.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/jquery-ui.min.css">

    <script src="/resources/js/jquery.js"></script>
    <script src="/resources/js/jquery.dataTables.min.js"></script>
    <script src="/resources/js/jquery-ui.min.js"></script>
    <script src="/resources/js/bootstrap.min.js"></script>
    <script src="/resources/js/app.js"></script>
    <script src="/resources/js/bootstrap-select.min.js"></script>
    <script src="/resources/js/words.js"></script>
</head>
<body>
<div class="container">
    <div class="navbar navbar-inverse navbar-fixed-top">
        <ul class="nav navbar-nav navbar-right lang">
            <li value="rus"><img width="45px" class="lang topbutton" src="/resources/images/ru.svg"></li>
            <li value="eng"><img width="45px" class="lang topbutton grayscale" src="/resources/images/en.svg"></li>
        </ul>
        <div class="container">
            <ul class="nav navbar-nav">
                <li><p><a href="/" class="btn btn-success topbutton">Test</a></p></li>
                <z:auth authorized="true">
                    <li><button id="btn-add-word" class="btn btn-info topbutton" data-toggle="modal" data-target="#modal-edit">Add word <i class="fa fa-plus"></i></button></li>
                </z:auth>
                <li><a href="/groups">GROUPS</a></li>
                <z:auth authorized="true" admin="true">
                    <li><a href="/words">USERS</a></li>
                </z:auth>
            </ul>
            <z:auth authorized="false">
                <form onsubmit="logIN(); return false;" method="post" class="navbar-form navbar-right">
                    <div class="form-group">
                        <input type="text" id="input-login" name="login" class="form-control" placeholder="login" required>
                        <input type="password" id="input-password" name="password" class="form-control" placeholder="password" required>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        ENTER<i class="fa fa-sign-in"></i>
                    </button>
                    <button type="button" class="btn btn-info" data-toggle="modal" data-target="#user-registration">
                        REGISTRATION
                    </button>
                </form>
            </z:auth>
            <z:auth authorized="true">
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${user.login}<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="#">profile</a> </li>
                            <li><a href="/logout?path=words">exit</a> </li>
                        </ul>
                    </li>
                </ul>
            </z:auth>
        </div>
    </div>
    <br><br><br>
    <div class="row">
        <div class="col-md-12">
            <div class="table-responsive">
                <table id="checked-table" class="table table-bordred table-striped" align="center">
                    <thead>
                    <th width="20"><input type="checkbox" id="checkall" /></th>
                    <th>Native</th>
                    <th>Translation</th>
                    <th width="20"></th>
                    <th>Group</th>
                    <th>Description</th>
                    <th>Comment</th>
                    <z:auth authorized="true">
                        <th width="20">Edit</th>
                        <th width="20">Delete</th>
                    </z:auth>
                    <z:auth authorized="false">
                        <th width="20" hidden>Edit</th>
                        <th width="20" hidden>Delete</th>
                    </z:auth>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>
<z:auth authorized="true">
<div class="modal fade" id="modal-edit">
    <div class="modal-dialog">
        <div class="modal-content">
            <form class="form-horizontal" onsubmit="buttonSaveWordClick(); return false;" method="post">
                <div class="modal-header">
                    <button class="close modal-clear" type="button" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Add / edit word</h4>
                </div>
                <div class="modal-body">
                    <div>
                        <div class="form-group">
                            <div class="col-md-10">
                                <input id="input-native-word" type="text" name="native_word" class="form-control" placeholder="native word" required="">
                            </div>
                            <div class="col-md-2">
                                <select id="select-native-language" class="form-control">
                                    <option value="rus">rus</option>
                                    <option value="eng">eng</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-12">
                                <input id="input-description" name="description" class="form-control" placeholder="description">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-12">
                                <textarea id="input-comment" type="text" name="comment" class="form-control textarea-modal" placeholder="comment" ></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-12">
                                <select id="select-groups" name="group_id" class="form-control" placeholder="group">
                                    <c:forEach  var="group" items="${groups}">
                                        <option value="${group.id}">${group.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <input id="input-id-word" name="id" type="hidden" value="">
                        <input id="input-language" name="language" type="hidden" value="rus">
                    </div>
                    <div id="div-modal-translation">
                        <div class="form-group non-delete">
                            <div class="col-md-12">
                                <input type="text" id="input-translation" name="translation" class="form-control translation" placeholder="translation" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <button class="btn btn-primary btn-large btn-block" type="button" id="btn-add-translation">&plus; Add translation</button>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-success" id="btn-save" type="submit">save</button>
                    <button class="btn btn-danger modal-clear" id="btn-close" type="button" data-dismiss="modal">close</button>
                </div>
            </form>
        </div>
    </div>
</div>
</z:auth>
<z:auth authorized="false">
    <div class="modal fade" id="user-registration" role="dialog">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <form onsubmit="userRegistration(); return false;"  method="post">
                    <div class="modal-header">
                        <button class="close modal-clear" type="button" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Registration</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group has-feedback" id="div-user-login">
                            <input id="input-user-login" type="text" name="login" class="form-control" placeholder="login" required>
                            <span id="icon-user-login-ok" class="glyphicon glyphicon-ok form-control-feedback" hidden></span>
                            <span id="icon-user-login-warning" class="glyphicon glyphicon-warning-sign form-control-feedback" hidden></span>
                        </div>
                        <div class="form-group">
                            <div class="alert alert-danger" id="alert-user-login-danger" style="padding: 8px; margin-bottom: 0px" hidden></div>
                        </div>
                        <div class="form-group">
                            <input id="input-user-pass" type="text" name="password" class="form-control" placeholder="password" required>
                        </div>
                        <div class="form-group">
                            <input id="input-user-email" type="text" name="email" class="form-control" placeholder="e-mail">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-success" id="btn-user-registration" type="submit">registration</button>
                        <button class="btn btn-danger modal-clear" id="btn-close-user-registration" type="button" data-dismiss="modal">close</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</z:auth>
</body>
</html>
