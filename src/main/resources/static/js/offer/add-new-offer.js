$(document).ready(function(){
    findInterviewsByCandidateId();
});
let findInterviewsByCandidateId = function () {
    let candidateId = document.getElementById('candidate').value;
    let dataInterview;
    let dataInterviewNote;
    $.ajax({
        method: "GET",
        url: "/api/offer/candidate/" + candidateId,
        async: false,
    }).done(function (data) {
        dataInterview = data;

        let htmlInterviewList = "";
        for (let interview of dataInterview) {
            htmlInterviewList += `<option value="${interview.id}">${interview.scheduleTitle}</option>`
        }

        if (dataInterview != null) {
            document.getElementById('interview').innerHTML = htmlInterviewList;
        } else {
            document.getElementById('interview').innerHTML = "";
        }

        findCandidateEmail();
        findInterviewNotesAndInterviewer();
    });

}

let findCandidateEmail = function () {
    let candidateId = document.getElementById('candidate').value;
    let candidateData;
    $.ajax({
        method: "GET",
        url: "/api/offer/candidateEmail/" + candidateId,
        async: false,
    }).done(function (data) {
        candidateData = data;
        if (candidateData != null) {
            document.getElementById('candidateEmail').innerText = candidateData.email;
        } else {
            document.getElementById('candidateEmail').innerText = '';
        }
    });
}

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

let assignMe = function () {
    let idCurrentUser = document.querySelector('.idCurrentUser').textContent;
    document.getElementById('recruiter').value = idCurrentUser;
}