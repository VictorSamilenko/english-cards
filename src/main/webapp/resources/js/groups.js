var table;

function refreshGroups() {
    table = $('#checked-table').DataTable({
        "ajax": {
            "dataType": 'json',
            "contentType": "application/json; charset=utf-8",
            "type": "GET",
            "url": "/groups/get",
            "dataSrc": "groups"
        },
        "columns": [
            {"data": null},
            {"data": "name"},
            {"data": "comment"},
            {"data": null},
            {"data": null}
        ],
        'columnDefs': [{
            'targets': 0,
            'searchable': false,
            'orderable': false,
            'className': 'exclude dt-body-center',
            'render': function (data, type, full, meta) {
                return '<input type="checkbox" id="group-' + data.id + '" class="exclude" name="id[]" width="20" value="'
                    + $('<div/>').text(data).html() + '">'
            },
            createdCell: function (td, cellData, rowData, row, col) {
                $(td).closest('tr').attr('group-id', rowData.id);
            }
        }, {
            'targets': 3,
            'searchable': false,
            'orderable': false,
            'className': 'exclude dt-body-center edit',
            'render': function (data, type, full, meta) {
                return '<p data-placement="top" data-toggle="tooltip" title="Edit"> ' +
                    '<a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#modal-edit" ;"> ' +
                    '<span class="glyphicon glyphicon-pencil"></span> ' +
                    '</a> ' +
                    '</p> '
            }
        }, {
            'targets': 4,
            'searchable': false,
            'orderable': false,
            'className': 'exclude dt-body-center delete',
            'render': function (data, type, full, meta) {
                return '<p data-placement="top" data-toggle="tooltip" title="Delete">' +
                    '<button class="btn btn-danger btn-xs" data-toggle="modal">' +
                    '<span class="glyphicon glyphicon-trash"></span>' +
                    '</button>' +
                    '</p>'
            }
        }]
    });
}

function buttonSaveGroupClick() {
    $.ajax({
        method: "POST",
        url: "/groups",
        data:{
            'id':$('#input-id-group').val(),
            'name':$('#input-name').val(),
            'comment':$('#input-comment').val()
        },
        success: function (response) {
            if(response.status==200)
                if (response.action == "update") {
                    table
                        .row('[group-id = ' + response.group.id + ']')
                        .data(response.group)
                        .draw();
                } else {
                    table
                        .row
                        .add(response.group)
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

$(document).ready(function (){
    refreshGroups();

    $('#checked-table tbody').on('click', 'td.edit', function () {
        var group = table.row($(this).parents('tr')).data();
        console.log(group);
        $('#input-id-group').val(group.id);
        $('#input-name').val(group.name);
        $('#input-comment').val(group.comment);

    });

    $('#checked-table tbody').on('click', 'td.delete', function () {
        var row = table.row($(this).parents('tr'));
        $.ajax({
            method: 'DELETE',
            url: '/groups/'+row.data().id,
            success: function (response) {
                console.log(response);
                row.remove().draw(false);
            }
        });

    });
});