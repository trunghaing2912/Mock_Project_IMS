const contextPath= "";
var roles = "";
var searchText = "";
var status = "";
var totalPage = 0;
var pageIndex = 0;
var pageIndexNext = 0;
var pageIndexPrev = 0;
var jobSkillId = 0;
let countRenderSkill = 0;
let countFetchSkill = 0;
let countRenderLevel = 0;
let countFetchLevel = 0;

// const myTimeout = setTimeout(hiddenCreatedMessage, 7000);

function renderData(url){

    let fet = fetch(url)
        .then(response => response.json())
        .then(page => {
            totalPage = page.totalPage;
            pageIndex = page.pageIndex;
            let jobs = page.data;
            roles = page.role;

            let trows = "";

            if (jobs.length == 0) {
                trows = `<p class="not-found">No job has been found.</p>`;
            } else {
                for (let job of jobs) {
                    trows += `<tr>
                              <td>${job.title}</td>
                              <td id="${job.id}" class="skill ${job.id} commaSkill"><span>skill_template</span></td>
                              <td>${job.startDate}</td>
                              <td>${job.endDate}</td>
                              <td id="level-${job.id}" class="level ${job.id} commaLevel"><span>level_template</span></td>
                              <td>${job.status}</td>
                              <td class="action-role">
                                <a href="${contextPath}/job/view/${job.id}"><i class="fa-solid fa-eye detail"></i></a>
                                <a href="${contextPath}/job/edit/${job.id}?prevPage=list"><i class="fa-solid fa-pen-nib edit"></i></a>
                                <a href="${contextPath}/job/delete/${job.id}" class="delete"><i class="fa-solid fa-trash-can delete"></i></a>
                              </td>
                            </tr>`
                }
            }
            // phần delete

            $(document).ready(function() {
                $(".delete").on("click", function(e) {
                    e.preventDefault();
                    let link = $(this);
                    let deleteUrl = link.attr("href");
                    $("#modal-title").html("Delete confirmation")
                    $("#confirmText").html("Do you want to delete this job?");
                    $("#confirmModal").modal('show');
                    $('#yesBtn').on("click",function (){
                        window.location.href=deleteUrl;
                    })
                    $('.fade').removeClass('modal-backdrop')
                });
                $("#confirmModal .modal-footer .btn-secondary, #confirmModal .modal-header .close").on("click", function () {
                    $("#confirmModal").modal('hide');
                });

            });
            // hết phần delete

            document.getElementsByTagName("tbody")[0].innerHTML = trows;

            let paging = "";
            for (let i=1; i<= page.totalPage; i++) {
                let active = i==page.pageIndex ? "active" : "";
                paging += `<li class="${active}" pageIndex="${i}" onclick="paging(event)" style="width: 30px; height: 30px; color: #5D87FF; text-align: center; line-height: 30px; border: 1px solid #5D87FF; cursor: pointer">${i}</li>`;
            }

            document.getElementsByClassName("pagination")[0].innerHTML = paging;

            displaySkill();
            displayLevel();
            renderAction();
        })
        .catch(error => {
            console.log(error)
        })
}
function paging(event){

    event.preventDefault();
    let targetElement = event.target;
    if (searchText=="") {
        renderData("/api/jobs?pageIndex=" + targetElement.getAttribute("pageIndex"));
    } else {
        renderData("/api/jobs?pageIndex=" + targetElement.getAttribute("pageIndex")+"&search="+searchText+"&status="+status);
    }
}

function next(event) {

    event.preventDefault();
    if (pageIndex >= totalPage){
        pageIndexPrev = pageIndex;
    } else {
        pageIndexPrev = pageIndex+1;
    }

    if (searchText=="") {
        renderData("/api/jobs?pageIndex=" + pageIndexPrev);
    } else {
        renderData("/api/jobs?pageIndex=" + pageIndexPrev + "&search="+searchText+"&status="+status);
    }

}

function prev(event) {

    event.preventDefault();
    if (pageIndex <= 1){
        pageIndexNext = pageIndex;
    } else {
        pageIndexNext = pageIndex-1;
    }

    if (searchText=="") {
        renderData("/api/jobs?pageIndex=" + pageIndexNext);
    } else {
        renderData("/api/jobs?pageIndex=" + pageIndexNext+"&search="+searchText+"&status="+status);
    }
}

search = function (event){

    event.preventDefault();
    searchText = document.getElementById("searchInput").value;

    status1 = document.getElementsByTagName("option");
    status = Array.from(status1).filter(x=>x.selected)[0].value;

    if (status == "" && searchText == ""){
        renderData("/api/jobs");
    } else {
        renderData("/api/jobs?search="+searchText+"&status="+status);
    }

}

findSkills = function (id) {

    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/jobs/skills/" + id,
        async: false,
    })
        .done(function (data) {
            renderData("/api/jobs?pageIndex=" + pageIndex+"&search="+searchText+"&status="+status);
        })
        .fail((error) => {
            console.error(error);
        });
}

function renderSkill(idSkill) {

    let i = 0;
    let url = "/api/jobs/skills/" + idSkill;

    let fetSkill = fetch(url)
        .then(response => response.json())
        .then(list => {

            let skills = list.skills;
            let htmlSkill = "";

            for (let skill of skills) {
                htmlSkill += `<span>${skill.name}, </span>`;
            }

            document.getElementById(idSkill).innerHTML = htmlSkill;
        })
        .then(function () {
            countFetchSkill++;
            if (countRenderSkill==countFetchSkill){
                deleteCommaSkill();
            }
        })
        .catch(error => {
            console.log(error)
        })
}

function displaySkill() {

    document.querySelectorAll(".skill").forEach(function (e,i) {
        countRenderSkill++;

        if (countRenderSkill==(document.querySelectorAll(".skill").length)-1){
            countRenderSkill=(document.querySelectorAll(".skill").length)-1;
        }

        renderSkill(e.id);
    })
}

function renderLevel(idLevel) {

    let i = 0;
    let idFetchLevel = idLevel.substring(6, Number.MAX_VALUE);
    let url = "/api/jobs/levels/" + idFetchLevel;

    let fetLevel = fetch(url)
        .then(response => response.json())
        .then(list => {

            let levels = list.levels;
            let htmlLevel = "";

            for (let level of levels) {
                htmlLevel += `<span>${level.name}, </span>`;
            }

            document.getElementById(idLevel).innerHTML = htmlLevel;
        })
        .then(function () {
            countFetchLevel++;
            if (countRenderLevel==countFetchLevel){
                deleteCommaLevel();
            }
        })
        .catch(error => {
            console.log(error)
        })
}

function displayLevel() {

    document.querySelectorAll(".level").forEach(function (e,i) {
        countRenderLevel++;
        if (countRenderLevel==(document.querySelectorAll(".level").length)-1){
            countRenderLevel=(document.querySelectorAll(".level").length)-1;
        }

        renderLevel(e.id);
    })
}

function deleteCommaSkill() {

    countRenderSkill=0;
    countFetchSkill=0;

    document.querySelectorAll(".commaSkill").forEach(function (e, i) {
        let commaElementSkill = (e.querySelectorAll("span"));
        let tempData = commaElementSkill[commaElementSkill.length - 1].innerText;

        commaElementSkill[commaElementSkill.length - 1].innerText=tempData.slice(0, -1);
    })
}

function deleteCommaLevel() {

    countRenderLevel=0;
    countFetchLevel=0;

    document.querySelectorAll(".commaLevel").forEach(function (e, i) {
        let commaElementLevel = (e.querySelectorAll("span"));
        let tempData1 = commaElementLevel[commaElementLevel.length - 1].innerText;

        commaElementLevel[commaElementLevel.length - 1].innerText=tempData1.slice(0, -1);
    })
}

// function hiddenCreatedMessage() {
//     document.querySelector(".creatMessage").classList.add("hidden");
// }

function showImport() {
    document.querySelector(".import-job").classList.remove("hidden");
}

function hiddenImport() {
    document.querySelector(".import-job").classList.add("hidden");
}

function renderAction() {
    if (roles == "[ROLE_INTERVIEWER]") {
        document.querySelectorAll(".edit").forEach((e, i) => {
            e.classList.add("hidden")
        });
        document.querySelectorAll(".delete").forEach((e, i) => {
            e.classList.add("hidden")
        });
    }
}

renderData("/api/jobs");
