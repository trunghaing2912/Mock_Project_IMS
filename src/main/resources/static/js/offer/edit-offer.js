$(document).ready(function(){
    findInterviewNotesAndInterviewer();
});
let findInterviewNotesAndInterviewer = function () {
    let interviewId = document.getElementById('interview').value;
    $.ajax({
        method: "GET",
        url: "/api/offer/interview/" + interviewId,
        async: false,
    }).done(function (data) {
        dataInterviewNote = data;

        if (dataInterviewNote != null) {
            document.getElementById('interviewNotes').value = dataInterviewNote.notes;
            if (dataInterviewNote.notes == "") {
                document.getElementById('interviewNotes').value = "N/A";
            }
        } else {
            document.getElementById('interviewNotes').value = "N/A";
        }
    });

    $.ajax({
        method: "GET",
        url: "/api/offer/interviewer/" + interviewId,
        async: false,
    }).done(function (data) {
        dataInterviewer = data;
        console.log(data)
        let listInterviewer = "";
        for (let interviewer of dataInterviewer) {
            listInterviewer += `<span class="interviewerName">${interviewer.interviewerName} </span>`;
        }
        if (dataInterviewer != null) {
            document.getElementById('interviewerList').innerHTML = listInterviewer;
        } else {
            document.getElementById('interviewerList').innerText = "";
        }
    });

}

const assignmeElement = document.getElementById('assign-me');
if(assignmeElement!=null){
    assignmeElement.addEventListener("click", function (ev) {
        ev.preventDefault();
        var assignedRecruiter = this.getAttribute('data');
        console.log(assignedRecruiter);

        var recruiterSelect = document.getElementById('recruiter');
        var options = recruiterSelect.options;
        console.log(options)

        for (var i = 0; i < options.length; i++) {
            if (options[i].value === assignedRecruiter) {
                console.log(options[i].value)
                options[i].selected = true;
                break;
            }
        }
    });
}
