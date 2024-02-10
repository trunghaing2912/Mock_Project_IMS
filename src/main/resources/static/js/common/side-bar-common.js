$(document).ready(function () {
    activeSideBar();
});

function activeSideBar() {
    let title =  document.getElementById('title').innerText;
    if (title == "Home Page") {
        document.getElementById('home-title').classList.add('active-title')
        document.getElementById('candidate-title').classList.remove('active-title')
        document.getElementById('job-title').classList.remove('active-title')
        document.getElementById('interview-title').classList.remove('active-title')
        document.getElementById('offer-title').classList.remove('active-title')
        document.getElementById('user-title').classList.remove('active-title')
    } else if (title == "Candidate") {
        document.getElementById('home-title').classList.remove('active-title')
        document.getElementById('candidate-title').classList.add('active-title')
        document.getElementById('job-title').classList.remove('active-title')
        document.getElementById('interview-title').classList.remove('active-title')
        document.getElementById('offer-title').classList.remove('active-title')
        document.getElementById('user-title').classList.remove('active-title')
    } else if (title == "Job") {
        document.getElementById('home-title').classList.remove('active-title')
        document.getElementById('candidate-title').classList.remove('active-title')
        document.getElementById('job-title').classList.add('active-title')
        document.getElementById('interview-title').classList.remove('active-title')
        document.getElementById('offer-title').classList.remove('active-title')
        document.getElementById('user-title').classList.remove('active-title')
    } else if (title == "Interview") {
        document.getElementById('home-title').classList.remove('active-title')
        document.getElementById('candidate-title').classList.remove('active-title')
        document.getElementById('job-title').classList.remove('active-title')
        document.getElementById('interview-title').classList.add('active-title')
        document.getElementById('offer-title').classList.remove('active-title')
        document.getElementById('user-title').classList.remove('active-title')
    } else if (title == "Offer") {
        document.getElementById('home-title').classList.remove('active-title')
        document.getElementById('candidate-title').classList.remove('active-title')
        document.getElementById('job-title').classList.remove('active-title')
        document.getElementById('interview-title').classList.remove('active-title')
        document.getElementById('offer-title').classList.add('active-title')
        document.getElementById('user-title').classList.remove('active-title')
    } else if (title == "User") {
        document.getElementById('home-title').classList.remove('active-title')
        document.getElementById('candidate-title').classList.remove('active-title')
        document.getElementById('job-title').classList.remove('active-title')
        document.getElementById('interview-title').classList.remove('active-title')
        document.getElementById('offer-title').classList.remove('active-title')
        document.getElementById('user-title').classList.add('active-title')
    }
}



