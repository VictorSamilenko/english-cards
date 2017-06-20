var word;
var words = [];
var tmpWords = [];
var maxProgressValue;
var currentProgressValue = 0;
var translations = [];
var translationInput;
var translationDiv;
var showTranslation = false;
var table;

function refreshWords(lang) {
    $('#loading').show();
    $('#word').html('');
    loadTestWords(lang, 0, 20);
    if (table != undefined)
        table.destroy();
    table = $('#checked-table').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            url: "/words/get?language=" + lang,
            dataSrc: "words",
        },
        initComplete: function (response) {
            maxProgressValue = response.json.iTotalRecords;
        },

        columns: [
            {data: "native"},
            {data: null},
            {data: "group"},
            {data: "group_id"}
        ],
        columnDefs: [{
            targets: 1,
            render: function (data, type, full, meta) {
                if (data.translation.length > 0) {
                    return data.translation[0].native
                }
                return '';
            }

        }, {
            targets: 3,
            visible: false
        }]
    });
}

function loadTestWords(lang, offset, limit) {
    $.ajax({
        url: "/words/get?language=" + lang + "&start=" + offset + "&length=" + limit,
        success: function (response) {
            words = words.concat(response.words.shuffle());
            if (offset == 0) {
                resetData(words);
            }
            ;
        }
    })
}

function resetData(words) {
    words.shuffle();
    currentProgressValue = 0;
    currentWrongValue = 0;
    $('#progress-bar-complete').prop("aria-valuemax", maxProgressValue);
    $('#progress-bar-wrong').prop("aria-valuemax", maxProgressValue);
    refreshData();
}

function refreshData() {
    $('#loading').hide();
    if (currentProgressValue + currentWrongValue >= words.length) {
        resetData(words);
    }
    word = words[currentProgressValue + currentWrongValue];
    $('#word').html(word.native);
    $('#progress-bar-complete').css("width", Math.round(currentProgressValue / maxProgressValue * 1000) / 10 + '%');
    $('#progress-complete').html(Math.round(currentProgressValue / maxProgressValue * 1000) / 10 + '%');
    $('#progress-bar-wrong').css("width", Math.round(currentWrongValue / maxProgressValue * 1000) / 10 + '%');
    $('#progress-wrong').html(Math.round(currentWrongValue / maxProgressValue * 1000) / 10 + '%');

    $('#result-group').html('');
    translationInput.val('');
    translationDiv.removeClass("has-warning has-success");
    $('#icon-ok').hide();
    $('#icon-warning').hide();
    $('#result-group').slideUp(300);

    translations = [];
    var i;
    for (i = 0; i < word.translation.length; i++) {
        translations.push(word.translation[i].native);
        $('#result-group').append(
            '<div class="panel panel-default">' +
            '<div class="panel-heading" data-toggle="collapse" data-parent="#result-group" href="#collapse' + word.translation[i].id + '">' +
            '<h4 class="panel-title">' +
            translations[i] +
            '</h4>' +
            '</div>' +
            '<div id="collapse' + word.translation[i].id + '" class="panel-collapse collapse' + ((i == 0) ? ' in' : '') + '">' +
            '<div class="panel-body">' + ((word.translation[i].comment != null) ? word.translation[i].comment : '') + '</div>' +
            '</div>' +
            '</div>');
    }
}

function buttonNextWordClick() {
    if (showTranslation)
        currentWrongValue++;
    else currentProgressValue++;
    if ((words.length < maxProgressValue) &&
        (words.length - currentProgressValue + currentWrongValue == 2 ||
        (currentProgressValue + currentWrongValue > words.length * 0.9)))
        loadTestWords(lang, words.length, Math.round(words.length * 0.5));
    refreshData();
    showTranslation = false;
}

function buttonSaveWordClick(event) {
    var translations = "";
    $('#div-modal-translation .translation').each(function () {
        translations += this.value.toLowerCase() + ',';
    })

    translations = translations.substring(0, translations.length - 1);

    var lang = $('#select-native-language').val()
    $.ajax({
        method: "POST",
        url: "/words",
        data: {
            'native_word': $('#input-native-word').val().toLowerCase(),
            'language': lang,
            'group_id': $('#select-groups-quick').val(),
            'translations': translations
        },
        success: function (data) {
            if (data.status == 200)
                $('#btn-close-quick-modal').click();
            else {
                console.log(data);
                alert(data.status);
            }
        }
    });
}

function userRegistration() {
    $('#div-user-login').removeClass("has-warning has-success");
    $('#icon-user-login-ok').hide();
    $('#icon-user-login-warning').hide();
    $('#alert-user-login-danger').slideUp('300');
    $.ajax({
        method: "POST",
        url: "/user",
        data: {
            'login': $('#input-user-login').val(),
            'password': $('#input-user-pass').val(),
            'email': $('#input-user-email').val()
        },
        success: function (data) {
            $('#alert-success').html(data + '<span class="close" data-dismiss="alert">×</span>')
            $('#alert-success').show();
            $('#btn-close-user-registration').click();
        },
        error: function (data) {
            if (data.status == 403) {
                $('#div-user-login').addClass("has-warning");
                $('#icon-user-login-warning').show();
                $('#alert-user-login-danger').html("User already exists");
                $('#alert-user-login-danger').slideDown('500');
            }
        }
    });
}

$(document).ready(function () {
    translationInput = $('#translation');
    translationDiv = $('#div-translation');

    setAutocomplete('#input-translation');
    refreshWords(lang);

    $('#input-user-login').focusout(function () {
        $('#div-user-login').removeClass("has-warning has-success");
        $('#icon-user-login-ok').hide();
        $('#icon-user-login-warning').hide();
        $('#alert-user-login-danger').slideUp('300');
        $.ajax({
            method: "GET",
            url: "/login/validate",
            data: {'login': $('#input-user-login').val()},
            success: function (data) {
                if (data == "valid") {
                    $('#div-user-login').addClass("has-success");
                    $('#icon-user-login-ok').show();

                } else {
                    $('#div-user-login').addClass("has-warning");
                    $('#icon-user-login-warning').show();
                }
            }
        })
    });

    translationInput.keypress(function (event) {
        if (translationDiv.hasClass("has-success") && event.keyCode == 13) {
            buttonNextWordClick();
            event.preventDefault();
        } else {
            translationDiv.removeClass("has-warning has-success");
            $('#icon-ok').hide();
            $('#icon-warning').hide();

            if (event.keyCode == 13) {
                if ($.inArray(translationInput.val().toLowerCase(), translations) != -1) {
                    translationDiv.addClass("has-success");
                    $('#icon-ok').show();
                } else {
                    translationDiv.addClass("has-warning");
                    $('#icon-warning').show();
                }
                event.preventDefault();
            }
        }
    });

    $('#btn-add-translation').click(function () {
        var count = $('#div-modal-translation .translation').length;
        $('#div-modal-translation').append('<div class="form-group"><div class="col-md-12"><input id="input-translation' + count + '" type="text" class="form-control translation" placeholder="translation"/></div></div>');
        setAutocomplete('#input-translation' + count);
    })

    $('#btn-close-quick-modal').click(function () {
        $('#div-modal-translation .form-group').not('.non-delete').remove();
    })

    $('#select-groups-modal').change(function () {
        var groupId = $('#select-groups-modal option:selected').val();
        if (groupId != -1)
            table
                .columns(3)
                .search(groupId)
                .draw();
        else
            table
                .columns(3)
                .search("")
                .draw();
    })

    $('#show-translation').click(function () {
        showTranslation = true;
        var resultGroup = $('#result-group');
        if (resultGroup.is(':visible')) {
            resultGroup.slideUp(500)
        } else {
            resultGroup.slideDown(700);
        }
    });

    $('#checked-table tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        }
        else {
            tmpWords.splice(0, 0, table.rows($(this)).data()[0]);
            $(this).addClass('selected');
        }
    });

    $('#btn-test').click(function () {
        if (tmpWords.length > 0) {
            words = tmpWords;
            tmpWords = [];
            resetData(words);
            maxProgressValue = words.length;
        }
    })
});