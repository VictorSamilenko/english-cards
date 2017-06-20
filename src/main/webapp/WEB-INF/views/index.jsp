<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tag/AuthTag.tld" prefix="z" %>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>English Cards Test</title>
    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/jquery.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/style.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/jquery-ui.min.css">

    <script src="/resources/js/jquery.js"></script>
    <script src="/resources/js/jquery.dataTables.min.js"></script>
    <script src="/resources/js/bootstrap.min.js"></script>
    <script src="/resources/js/app.js"></script>
    <script src="/resources/js/jquery-ui.min.js"></script>
    <script src="/resources/js/index.js"></script>
</head>

<body>

<div class="container">

    <div class="navbar navbar-inverse navbar-fixed-top">
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right lang">
                <li value="rus"><img width="45px" class="lang topbutton " src="/resources/images/ru.svg"></li>
                <li value="eng"><img width="45px" class="lang topbutton grayscale" src="/resources/images/en.svg"></li>
            </ul>

            <div class="container">
                <ul class="nav navbar-nav">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">WORDS<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="/words">All words</a></li>
                            <li><a href="#" data-toggle="modal" data-target="#select-words-modal">Select words for test</a></li>
                            <z:auth authorized="true">
                                <li><a href="#" data-toggle="modal" data-target="#quick-add-words-modal">Quick add words</a></li>
                            </z:auth>
                        </ul>
                    </li>
                    <li><a href="/groups">GROUPS</a></li>
                    <z:auth authorized="true" admin="true">
                        <li><a href="/users">USERS</a></li>
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
                                <li><a href="/logout">exit</a> </li>
                            </ul>
                        </li>
                    </ul>
                </z:auth>
            </div>
        </div>
    </div>

    <div class="col-sm-6 col-sm-offset-3">
        <div class="progress">
            <div class="progress-bar progress-bar-success" id="progress-bar-complete" role="progressbar" aria-valuemin="0" aria-valuemax="100">
                <span id="progress-complete"></span>
            </div>
            <div class="progress-bar progress-bar-danger" id="progress-bar-wrong" role="progressbar" aria-valuemin="0" aria-valuemax="100">
                <span id="progress-wrong"></span>
            </div>
        </div>
    </div>

    <z:auth authorized="false">
        <div class="alert alert-danger" id="alert-danger" style="margin-top: 80px" hidden></div>
        <div class="alert alert-success" id="alert-success" style="margin-top: 80px" hidden></div>
    </z:auth>

    <div class="col-sm-6 col-sm-offset-3">
        <div class="main-form center">
            <form class="form-horizontal">
                <div class="form-group" align="center">
                    <img id="loading" src="/resources/images/loading.gif">
                   <h2 id="word"></h2>
                </div>
                <div class="form-group has-feedback" id="div-translation">
                    <label for="translation">Enter the translation and press "Enter"</label>
                    <input class="form-control input-lg" name="translation" id="translation" type="text" autocomplete="off" placeholder="translation" autofocus>
                    <span id="icon-ok" class="glyphicon glyphicon-ok form-control-feedback"></span>
                    <span id="icon-warning" class="glyphicon glyphicon-warning-sign form-control-feedback"></span>
                </div>
                <div class="form-group panel-group" id="result-group" hidden>
                </div>

                <div class="form-group">
                    <input class="btn btn-large btn-block btn-lg btn-primary" type="button" id="show-translation" value="Show translation">
                </div>

                <div class="form-group">
                    <input class="btn btn-large btn-block btn-lg btn-success" type="button" onclick="buttonNextWordClick()" value="Next word">
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="select-words-modal" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Select words for test</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6 col-md-offset-3">
                        <select id="select-groups-modal" class="form-control" placeholder="group">
                            <option value="-1">All groups</option>
                            <c:forEach  var="group" items="${groups}">
                                <option value="${group.id}">${group.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-12">
                        <div class="table-responsive">
                            <table id="checked-table" class="table table-bordred table-striped" align="center">
                                <thead>
                                <th>Native</th>
                                <th>Translation</th>
                                <th>Group</th>
                                <th hidden>GroupID</th>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-success" id="btn-test" data-dismiss="modal">test</button>
                <button class="btn btn-danger modal-clear" id="btn-close-select-modal" type="button" data-dismiss="modal">close</button>
            </div>
        </div>

    </div>
</div>
<z:auth authorized="true">
<div class="modal fade" id="quick-add-words-modal" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <form class="form-horizontal" onsubmit="buttonSaveWordClick(); return false;" method="post">
                <div class="modal-header">
                    <button class="close modal-clear" type="button" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Quick add word</h4>
                </div>
                <div class="modal-body">
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
                        <div class="col-md-6">
                            <select id="select-groups-quick" class="form-control" placeholder="group">
                                <c:forEach  var="group" items="${groups}">
                                    <option value="${group.id}">${group.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div id="div-modal-translation">
                        <div class="form-group non-delete">
                            <div class="col-md-12">
                                <input id="input-translation" type="text" name="translation" class="form-control translation" placeholder="translation">
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
                    <button class="btn btn-danger modal-clear" id="btn-close-quick-modal" type="button" data-dismiss="modal">close</button>
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
<!-- Yandex.Metrika counter --> <script type="text/javascript"> (function (d, w, c) { (w[c] = w[c] || []).push(function() { try { w.yaCounter41922869 = new Ya.Metrika({ id:41922869, clickmap:true, trackLinks:true, accurateTrackBounce:true }); } catch(e) { } }); var n = d.getElementsByTagName("script")[0], s = d.createElement("script"), f = function () { n.parentNode.insertBefore(s, n); }; s.type = "text/javascript"; s.async = true; s.src = "https://mc.yandex.ru/metrika/watch.js"; if (w.opera == "[object Opera]") { d.addEventListener("DOMContentLoaded", f, false); } else { f(); } })(document, window, "yandex_metrika_callbacks"); </script> <noscript><div><img src="https://mc.yandex.ru/watch/41922869" style="position:absolute; left:-9999px;" alt="" /></div></noscript> <!-- /Yandex.Metrika counter -->
</html>