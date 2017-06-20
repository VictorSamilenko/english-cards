var lang = "rus";

$(document).ready(function () {
    $("#checked-table #checkall").click(function () {
        if ($("#checked-table #checkall").is(':checked')) {
            $("#checked-table input[type=checkbox]").each(function () {
                $(this).prop("checked", true);
            });

        } else {
            $("#checked-table input[type=checkbox]").each(function () {
                $(this).prop("checked", false);
            });
        }
    });

    $("[data-toggle=tooltip]").tooltip();

    $('.modal-clear').click(function () {
        $('.modal input').val('');
        $('.modal textarea').val('');
        $('.modal select option').prop("selected", false);
        $('.dropdown-menu li').removeClass('selected');
        $('.modal-delete').remove();
        try {
            $('.selectpicker').val('').selectpicker('refresh');
        } catch (err) {
        }
        $('#checked-table tbody tr.selected').each(function () {
            $(this).removeClass('selected');
        });
    });

    $('ul.lang').on('click', 'li', function () {
        if ($(this).children('img.lang').hasClass('grayscale')) {
            lang = $(this).attr('value')
            refreshWords(lang);
            $('ul.lang img.lang').toggleClass('grayscale');
        }
    })

});

function setAutocomplete(id) {
    $(id).autocomplete({
        source: function (request, response) {
            var l = encodeURI(request.term);
            console.log(l);
            l = encodeURIComponent(request.term);
            console.log(l);
            $.ajax({
                url: "/words/autocomplete?criteria=" + l,
                dataType: "json",
                data: {
                    q: request.term
                },
                success: function (data) {
                    console.log(data);
                    response(data);
                }
            });
        },
        select: function (event, ui) {
            console.log(ui.item ? "Selected: " + ui.item.label : "Nothing selected, input was " + this.value);
        }
    });
}

function logIN() {
    $.ajax({
        method: "POST",
        url: "/login",
        data: {
            'login': $('#input-login').val(),
            'password': $('#input-password').val()
        },
        success: function () {
            window.location.reload();
        },
        error: function (data) {
            $('#alert-danger').html(data.responseText + '<span class="close" data-dismiss="alert">×</span>')
            $('#alert-danger').show();
        }

    });
}

Array.prototype.shuffle = function () {
    for (var i = this.length - 1; i > 0; i--) {
        var num = Math.floor(Math.random() * (i + 1));
        var d = this[num];
        this[num] = this[i];
        this[i] = d;
    }
    return this;
};

$(".dropdown-menu li a").click(function () {
    var selText = $(this).text();
    $(this).parents('.btn-group').find('.dropdown-toggle').html(selText + ' <span class="caret"></span>');
});
