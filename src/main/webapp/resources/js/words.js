var words = [];
var translations = [];
var table;

function refreshWords (lang) {
    if (table!=undefined)
        table.destroy();
    table = $('#checked-table').DataTable({
        select: true,
        processing: true,
        serverSide: true,
        ajax:{
            url:"/words/get?language="+lang,
            dataSrc: "words"
        },
        columns: [
            { data: null },
            { data: "native" },
            { data: null},
            { data: null},
            { orderable: false, data: "group" },
            { orderable: false, data: "description" },
            { orderable: false, data: "comment" },
            { data: null },
            { data: null }
        ],
        columnDefs: [{
            targets: 0,
            searchable:false,
            orderable:false,
            className: 'exclude dt-body-center',
            render: function (data, type, full, meta){
                return '<input type="checkbox" id="word-'+data.id+'" class="exclude" name="id[]" width="20" value="'
                    + $('<div/>').text(data).html() + '">'},
            createdCell: function(td, cellData, rowData, row, col) {
                $(td).closest('tr').attr('word-id', rowData.id);
            }
        },{
            targets: 2,
            orderable:false,
            render: function (data, type, full, meta) {
                if (data.translation.length > 0) {
                    return data.translation[0].native
                }
                return '';
            }

        },{
            targets: 3,
            searchable:false,
            orderable:false,
            className: 'exclude dt-body-center details',
            render: function (data, type, full, meta){
                if(data.translation.length>1)
                    return '<a><i class="fa fa-arrow-down"></i></a>';
                else return '';
            }
        },{
            targets: 7,
            searchable:false,
            orderable:false,
            className: 'exclude dt-body-center edit',
            render: function (data, type, full, meta){
                return '<p data-placement="top" data-toggle="tooltip" title="Edit"> '+
                    '<a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#modal-edit" ;"> '+
                    '<span class="glyphicon glyphicon-pencil"></span> '+
                    '</a> '+
                    '</p> '}
        },{
            targets: 8,
            searchable:false,
            orderable:false,
            className: 'exclude dt-body-center delete',
            render: function (data, type, full, meta){
                return '<p data-placement="top" data-toggle="tooltip" title="Delete">' +
                    '<button class="btn btn-danger btn-xs" data-toggle="modal">' +
                    '<span class="glyphicon glyphicon-trash"></span>' +
                    '</button>' +
                    '</p>'}
        }]
    });
}

function buttonSaveWordClick() {
    var translations = "";
    $('#div-modal-translation .translation').each(function(){
        translations+=this.value+',';
    })

    translations = translations.substring(0,translations.length-1);
    $.ajax({
        method: "POST",
        url: "/words",
        data:{
            'id':$('#input-id-word').val(),
            'native_word':$('#input-native-word').val(),
            'language':$('#select-native-language option:selected').val(),
            'group_id':$('#select-groups').val(),
            'description':$('#input-description').val(),
            'comment':$('#input-comment').val(),
            'translations':translations
        },
        success: function (response) {
            if(response.status==200)
                if (response.action == "update") {
                    table
                        .row('[word-id = ' + response.word.id + ']')
                        .data(response.word)
                        .draw();
                } else {
                    table
                        .row
                        .add(response.word)
                        .draw();
                }
            else {
                console.log(response);
                alert(data.status);
            }
        }
    });
    $('#btn-close').click();
}

$(document).ready(function () {
    setAutocomplete('#input-translation');
    refreshWords("rus");

    $('#select-native-language').change(function () {
        var s = $(this).val();
        $('#input-language').val(s);
    });

    $('#checked-table tbody').on('click', 'td.edit', function () {

        var tr = $(this).closest('tr');
        var row = table.row( tr );
        var word = row.data();

        $('#input-id-word').val(word.id);
        $('#select-groups option[value='+word.group_id+']').prop("selected", true);
        $('#select-native-language option[value="' + word.language + '"]').prop("selected", true);
        $('#input-language').val(word.language);
        $('#input-native-word').val(word.native);
        $('#input-description').val(word.description);
        $('#input-comment').val(word.comment);
        $('#input-translation').val(word.translation[0].native);
        translations=[];

        for (index = 0; index < word.translation.length; index++) {
            translations[index] = word.translation[index].id;
            if (index > 0) {
                $('#div-modal-translation').append('<div class="form-group modal-delete"><div class="col-md-12"><input id="input-translation' + index + '" type="text" class="form-control translation" placeholder="translation" value="' + word.translation[index].native + '"/></div></div>');
                setAutocomplete('#input-translation' + index);
            }
        }
        $("#select-native-language").change();
    });

    $('#checked-table tbody').on('click', 'td.details', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );
        var icon = tr.find(".details i.fa");
        icon.removeClass('fa-arrow-down fa-arrow-up');
        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
            icon.addClass('fa-arrow-down');
        }
        else {
            // Open this row
            row.child( format(row.data()) ).show();
            tr.addClass('shown');
            icon.addClass('fa-arrow-up');
        }
    });

    $('#checked-table tbody').on('click', 'td.delete', function () {
        var row = table.row($(this).parents('tr'));
        $.ajax({
            method: 'DELETE',
            url: '/words/'+row.data().id,
            success: function (response) {
                row.remove().draw(false);
            }
        });
    });

    $('ul.lang').on('click', 'li', function () {
        var lang = $(this).attr('value')
        refreshWords(lang);
    });

    $("#select-native-language").change(function () {
        var lang = $(this).find(":not(:selected)").val();
        if (lang == "rus") {

        } else {

        }
    });

    $('#btn-add-translation').click(function () {
        var count = $('#div-modal-translation .translation').length;
        $('#div-modal-translation').append('<div class="form-group modal-delete"><div class="col-md-12"><input id="input-translation'+count+'" type="text" class="form-control translation" placeholder="translation"/></div></div>');
        setAutocomplete('#input-translation'+count);
    })

    $('#btn-add-word').click(function () {
        $('#select-native-language option[value="' + lang + '"]').prop("selected", true);
    });
});

function format ( d ) {
    var resultTable = '<table cellpadding="5">';
    var index;
    for (index = 1; index < d.translation.length; index++) {
        resultTable +='<tr>'+
            '<td>'+d.translation[index].native+'</td>'+
            '<td>'+((!d.translation[index].description)?' ':d.translation[index].description)+'</td>'+
            '<td>'+((!d.translation[index].comment)?' ':d.translation[index].comment)+'</td>'+
            '</tr>';
    }
    resultTable+= '</table>';
    return resultTable;
}
