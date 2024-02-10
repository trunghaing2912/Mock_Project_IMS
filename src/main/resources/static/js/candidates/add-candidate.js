
new MultiSelectTag('skill');
document.getElementById('assign-me').addEventListener("click", function (ev) {
    ev.preventDefault();
    var assignedRecruiter = this.getAttribute('data');
    var recruiterSelect = document.getElementById('recruiter');

    recruiterSelect.value=assignedRecruiter;
});