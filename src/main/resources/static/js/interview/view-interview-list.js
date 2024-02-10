const contextPath = "";
var roles = "";
var searchText = "";
var field1 = "";
var field2 = "";
var totalPage = 0;
var pageIndex = 0;
var pageIndexNext = 0;
var pageIndexPrev = 0;
let countRenderInterviewer = 0;
let countFetchInterviewer = 0;

function renderData(url) {
    let fet = fetch(url)
        .then(response => response.json())
        .then(page => {
            totalPage = page.totalPage;
            pageIndex = page.pageIndex;
            roles = page.role;
            let interviews = page.data;

            let trows = "";
            if (interviews.length === 0) {
                trows = `<p class="not-found">No item matches with your search data. Please try
again!</p>`
            } else {
                for (let i of interviews) {
                    trows += `
                            <tr>
                              <td>${i.scheduleTitle}</td>
                              <td id="candidate-${i.id}" class="candidate"><span>candidate_template</span></td>
                              <td id="interviewer-${i.id}" class="interviewer ${i.id} commaInterviewer"><span>interviewer_template</span></td>
                              <td>${i.interviewDate}   ${i.startTime} - ${i.endTime}</td>
                              <td>${i.result}</td>
                              <td>${i.status}</td>
                              <td id="job-${i.id}" class="job"><span>job_template</span></td>
                              <td class="action-parent">
                                  <a href="${contextPath}/interview/view/${i.id}"><i class="fa-solid fa-eye"></i></a>
                                  <a href="${contextPath}/interview/edit/${i.id}?prevPage=list"><i class="fa-solid fa-pen-nib edit-candidate"></i></a>
                                  <a href="${contextPath}/interview/submit/${i.id}"><i class="fa-regular fa-paper-plane send-result"></i></a>
                              </td>
                            </tr>`
                }
            }

            let paging = "";
            for (let i = 1; i <= page.totalPage; i++) {
                let active = i == page.pageIndex ? "active" : "";
                paging += `<li class="${active}" pageIndex="${i}" onclick="paging(event)" style="width: 30px; height: 30px; color: #5D87FF; text-align: center; line-height: 30px; border: 1px solid #5D87FF; cursor: pointer">${i}</li>`;
            }

            document.getElementsByTagName("tbody")[0].innerHTML = trows;
            document.getElementsByClassName("pagination")[0].innerHTML = paging;
            displayCandidate();
            displayInterviewer();
            displayJob();
            renderAction();
        })
        .catch(error => {
            console.log(error)
        });
}

function paging(event) {

    event.preventDefault();
    let targetElement = event.target;

    if (searchText === "" && field1 === "" && field2 === "") {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex"));
    } else if (searchText === "" && field1 === "") {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex") + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex") + "&interviewer=" + field1);
    } else if (searchText === "") {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex") + "&interviewer=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&interviewer=" + field1);
    } else {
        renderData("/api/interview?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&interviewer=" + field1 + "&status=" + field2);
    }
}

function next(event) {

    event.preventDefault();
    if (pageIndex >= totalPage) {
        pageIndexPrev = pageIndex;
    } else {
        pageIndexPrev = pageIndex + 1;
    }

    if (searchText === "" && field1 === "" && field2 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexPrev);
    } else if (searchText === "" && field1 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexPrev + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexPrev + "&interviewer=" + field1);
    } else if (searchText === "") {
        renderData("/api/interview?pageIndex=" + pageIndexPrev + "&interviewer=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexPrev + "&search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexPrev + "&search=" + searchText + "&interviewer=" + field1);
    } else {
        renderData("/api/interview?pageIndex=" + pageIndexPrev + "&search=" + searchText + "&interviewer=" + field1 + "&status=" + field2);
    }

}

function prev(event) {

    event.preventDefault();
    if (pageIndex <= 1) {
        pageIndexNext = pageIndex;
    } else {
        pageIndexNext = pageIndex - 1;
    }

    if (searchText === "" && field1 === "" && field2 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexNext);
    } else if (searchText === "" && field1 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexNext + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexNext + "&interviewer=" + field1);
    } else if (searchText === "") {
        renderData("/api/interview?pageIndex=" + pageIndexNext + "&interviewer=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexNext + "&search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/interview?pageIndex=" + pageIndexNext + "&search=" + searchText + "&interviewer=" + field1);
    } else {
        renderData("/api/interview?pageIndex=" + pageIndexNext + "&search=" + searchText + "&interviewer=" + field1 + "&status=" + field2);
    }
}

search = function (event) {

    event.preventDefault();
    searchText = document.getElementById("searchInput").value;

    field1 = document.getElementsByClassName("searchSelection");
    field2 = document.getElementsByClassName("statusSelection");

    field1 = Array.from(field1).filter(x => x.selected)[0].value;
    field2 = Array.from(field2).filter(x => x.selected)[0].value;

    if (searchText === "" && field1 === "" && field2 === "") {
        renderData("/api/interview");
    } else if (searchText === "" && field1 === "") {
        renderData("/api/interview?" + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/interview?" + "&interviewer=" + field1);
    } else if (searchText === "") {
        renderData("/api/interview?" + "&interviewer=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/interview?search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/interview?search=" + searchText + "&interviewer=" + field1);
    } else {
        renderData("/api/interview?search=" + searchText + "&interviewer=" + field1 + "&status=" + field2);
    }
    console.log(searchText);
}

// chooseFieldSearch = function () {
//
//     field = document.getElementsByTagName("option");
//     field = Array.from(field).filter(x => x.selected)[0].value;
//
//     if (field == "startTime" || field == "endTime") {
//         html = `<input type="text" class="form-control mb-2" id="searchInput" placeholder="Keyword" style="width: 100%">`;
//         document.getElementById("search-input-parent").innerHTML = html;
//         document.getElementById("searchInput").type = "time";
//     } else if (field == "interviewDate") {
//         html = `<input type="text" class="form-control mb-2" id="searchInput" placeholder="Keyword" style="width: 100%">`;
//         document.getElementById("search-input-parent").innerHTML = html;
//         document.getElementById("searchInput").type = "date";
//     } else {
//         html = `<input type="text" class="form-control mb-2" id="searchInput" placeholder="Keyword" style="width: 100%">`;
//         document.getElementById("search-input-parent").innerHTML = html;
//         document.getElementById("searchInput").type = "text";
//     }
// }


findCandidate = function (id) {
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/interview/candidate/" + id,
        async: false
    }).done(function (data) {
        renderData("/api/interview?pageIndex=" + pageIndex + "&search=" + searchText + "&field=" + field);
    })
        .fail((error) => {
            console.error(error);
        });
}

function renderCandidate(idCandidate) {
    let idFetchCandidate = idCandidate.substring(10, Number.MAX_VALUE);
    let url = "/api/interview/candidate/" + idFetchCandidate;
    let fetCandidate = fetch(url)
        .then(response => response.json())
        .then(c => {
            let candidate = c.name;
            let htmlCandidate = "";
            htmlCandidate += `<span>${candidate}</span>`;
            document.getElementById(idCandidate).innerHTML = htmlCandidate;
        }).catch(error => {
            console.log(error)
        });

}

function displayCandidate() {
    document.querySelectorAll(".candidate").forEach(function (e, i) {
        renderCandidate(e.id);
    })
}

findInterviewer = function (id) {
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/interview/interviewer/" + id,
        async: false
    }).done(function (data) {
        renderData("/api/interview?pageIndex=" + pageIndex + "&search=" + searchText + "&field=" + field);
    })
        .fail((error) => {
            console.error(error);
        });
}

function displayInterviewer() {
    document.querySelectorAll(".interviewer").forEach(function (e, i) {
        countRenderInterviewer++;

        if (countRenderInterviewer === (document.querySelectorAll(".interviewer").length) - 1) {
            countRenderInterviewer = (document.querySelectorAll(".interviewer").length -1);
        }

        renderInterviewer(e.id);
    })
}

function renderInterviewer(idInterviewer) {
    let idFetchInterviewer = idInterviewer.substring(12, Number.MAX_VALUE);
    let url = "/api/interview/interviewer/" + idFetchInterviewer;

    let fetInterviewer = fetch(url)
        .then(response => response.json())
        .then(p => {
            let interviewers = p.interviewers;
            let htmlInterviewer = "";

            for (let i of interviewers) {
                htmlInterviewer += `<span>${i.name}, </span>`;
            }
            document.getElementById(idInterviewer).innerHTML = htmlInterviewer;
        }).then(function () {
            countFetchInterviewer++;

            if(countRenderInterviewer === countFetchInterviewer) {
                deleteCommaInterviewer();
            }
        })

        .catch(error => {
            console.log(error)
        });

}



function deleteCommaInterviewer() {

    countRenderInterviewer = 0;
    countFetchInterviewer = 0;

    document.querySelectorAll(".commaInterviewer").forEach(function (e, i) {
        let commaElementInterviewer = (e.querySelectorAll("span"));
        let tempData = commaElementInterviewer[commaElementInterviewer.length - 1].innerText;

        commaElementInterviewer[commaElementInterviewer.length - 1].innerText = tempData.slice(0, -1);
    })
}


findJob = function (id) {
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/interview/job/" + id,
        async: false
    }).done(function (data) {
        renderData("/api/interview?pageIndex=" + pageIndex + "&search=" + searchText + "&field=" + field);
    })
        .fail((error) => {
            console.error(error);
        });
}

function renderJob(idJob) {
    let idFetchJob = idJob.substring(4, Number.MAX_VALUE);
    let url = "/api/interview/job/" + idFetchJob;
    let fetJob = fetch(url)
        .then(response => response.json())
        .then(j => {
            let job = j.name;
            let htmlJob = "";
            htmlJob += `<span>${job}</span>`;
            document.getElementById(idJob).innerHTML = htmlJob;
        }).catch(error => {
            console.log(error)
        });

}

function displayJob() {
    document.querySelectorAll(".job").forEach(function (e, i) {
        renderJob(e.id);
    })
}


function renderAction() {
    if (roles == "[ROLE_INTERVIEWER]") {
        document.querySelectorAll(".edit-candidate").forEach((e, i) => {
            e.classList.add("hidden")
        });

    }

    if (roles == "[ROLE_ADMIN]" || roles == "[ROLE_MANAGER]" || roles == "[ROLE_RECRUITER]") {
        document.querySelectorAll(".send-result").forEach((e, i) => {
            e.classList.add("hidden")
        });
    }

}

renderData("/api/interview");