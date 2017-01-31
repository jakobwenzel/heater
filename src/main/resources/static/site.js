function getSelected() {
    return $('input[name=setting]:checked').val();
}

function sendImmediately() {
    var value = getSelected();
    $.ajax("/api/heater", {
        data: value,
        method: "POST",
        contentType: "text/plain",
        success: function() {
            alert("success");
        },
        error: function(err1, err2, err3) {
            alert("error: "+err3);
        }
    });
}

function uploadEntry(entry, isDelete, success) {
    console.log(entry);
    var url;
    if (isDelete)
        url = "/api/schedule/delete";
    else
        url = "/api/schedule";
    $.ajax(url, {
        data: JSON.stringify(entry),
        method: "POST",
        contentType: "application/json",
        success: loadSchedule,
        error: function(err1, err2, err3) {
            console.log(err1);
            alert("error: "+err3);
        }
    });
}

function schedule() {
    var value = getSelected();
    var timestamp = $("#datetime").getPickerDate().getTime();
    var data = {
        instant: timestamp/1000,
        setting: value
    };
    uploadEntry(data, false);
}

function formatScheduleEntry(entry) {
    var time = moment(entry.instant*1000)
    return "<tr><td>"+time.calendar()+"</td><td>"+entry.setting+"</td><td><input type='button' onclick='removeSchedule("+JSON.stringify(entry)+")' value='Delete'/> </td></tr>";
}

function removeSchedule(entry) {
    uploadEntry(entry, true);
}

function loadSchedule() {
    $.ajax("/api/schedule", {
        method: "GET",
        accept: "application/json",
        success: function(data) {
            var html = "<table><tr><th>Time</th><th>Setting</th><th></th></tr>";
            data.forEach(function (entry) {
                html+=formatScheduleEntry(entry);
            });
            html+="</table>";
            console.log(html);
            $("#scheduleList").html(html);
        },
        error: function(err1, err2, err3) {
            console.log(err1)
            alert("error: "+err3);
        }
    });

}
$.fn.setPickerDate = function(date) {
    $(this).val(
        moment(date).format("YYYY-MM-DDTHH:mm"));
}
$.fn.getPickerDate = function() {
    //return new Date($(this).prop('valueAsNumber'));
    var s = $(this).val();
    return moment(s).toDate();
}
$(function() {
    $("#datetime").setPickerDate(new Date());
    moment.locale("de");
    loadSchedule();
});
