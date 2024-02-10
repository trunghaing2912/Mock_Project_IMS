const contextPath= "";
var roles = "";
var searchText = "";
var field1 = "";
var field2 = "";
var totalPage = 0;
var pageIndex = 0;
var pageIndexNext = 0;
var pageIndexPrev = 0;
let countRenderOffer=0;
let countFetchOffer=0;

function renderData(url){
    console.log(url)
    let fet = fetch(url)
    .then(response=> response.json())
        .then(page=>{
            totalPage=page.totalPage;
            pageIndex=page.pageIndex;
            let offers=page.data;
            roles=page.role;

            let trows="";
            if(offers.length===0){
                trows = `<p class="not-found">No item matches with your search data. Please try again!</p>`
            }else{
                for(let i of offers){
                    trows += `
                            <tr>
                              
                              <td id="candidate-${i.id}" class="candidate"><span>candidate_template</span></td>
                              <td id="candidateEmail-${i.id}" class="candidateEmail"><span>candidate_template</span></td>
                              <td id="user-${i.id}" class="user" ><span>user_template</span></td>
                              <td id="department-${i.id}" class="department" ><span>department_template</span></td>
                              <td>${i.note}</td>
                              <td>${i.offerStatus}</td>
                              <td class="action-parent">
                                  <a href="${contextPath}/offer/view/${i.id}"><i class="fa-solid fa-eye"></i></a>
                                  <a href="${contextPath}/offer/edit/${i.id}"><i class="fa-solid fa-pen-nib edit-candidate"></i></a>
                              </td>
                            </tr>`
                }
            }
            let paging ="";
            for(let i=1;i<=page.totalPage;i++){
                let active=i == page.pageIndex ? "active":"";
                paging += `<li class="${active}" pageIndex="${i}" onclick="paging(event)" style="width: 30px; height: 30px; color: #5D87FF; text-align: center; line-height: 30px; border: 1px solid #5D87FF; cursor: pointer">${i}</li>`;
            }

            document.getElementsByTagName("tbody")[0].innerHTML=trows;
            document.getElementsByClassName("pagination")[0].innerHTML=paging;
            displayCandidateOffer();
            displayDepartment();
            displayCandidateEmail();
            displayUser();



        })
}
function paging(event) {

    event.preventDefault();
    let targetElement = event.target;

    if (searchText === "" && field1 === "" && field2 === "") {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex"));
    } else if (searchText === "" && field1 === "") {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex") + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex") + "&department=" + field1);
    } else if (searchText === "") {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex") + "&department=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&department=" + field1);
    } else {
        renderData("/api/offer?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&department=" + field1 + "&status=" + field2);
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
        renderData("/api/offer?pageIndex=" + pageIndexPrev);
    } else if (searchText === "" && field1 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexPrev + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexPrev + "&department=" + field1);
    } else if (searchText === "") {
        renderData("/api/offer?pageIndex=" + pageIndexPrev + "&department=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexPrev + "&search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexPrev + "&search=" + searchText + "&department=" + field1);
    } else {
        renderData("/api/offer?pageIndex=" + pageIndexPrev + "&search=" + searchText + "&department=" + field1 + "&status=" + field2);
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
        renderData("/api/offer?pageIndex=" + pageIndexNext);
    } else if (searchText === "" && field1 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexNext + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexNext + "&department=" + field1);
    } else if (searchText === "") {
        renderData("/api/offer?pageIndex=" + pageIndexNext + "&department=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexNext + "&search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/offer?pageIndex=" + pageIndexNext + "&search=" + searchText + "&department=" + field1);
    } else {
        renderData("/api/offer?pageIndex=" + pageIndexNext + "&search=" + searchText + "&department=" + field1 + "&status=" + field2);
    }
}

search = function (event) {

    event.preventDefault();
    searchText = document.getElementById("searchInput").value;
    console.log(searchText)

    field1 = document.getElementsByClassName("searchSelection");
    field2 = document.getElementsByClassName("statusSelection");

    field1 = Array.from(field1).filter(x => x.selected)[0].value;
    field2 = Array.from(field2).filter(x => x.selected)[0].value;

    if (searchText === "" && field1 === "" && field2 === "") {
        renderData("/api/offer");
    } else if (searchText === "" && field1 === "") {
        renderData("/api/offer?" + "&status=" + field2);
    } else if (searchText === "" && field2 === "") {
        renderData("/api/offer?" + "&department=" + field1);
    } else if (searchText === "") {
        renderData("/api/offer?" + "&department=" + field1 + "&status=" + field2);
    } else if (field1 === "") {
        renderData("/api/offer?search=" + searchText + "&status=" + field2);
    } else if (field2 === "") {
        renderData("/api/offer?search=" + searchText + "&department=" + field1);
    } else {
        renderData("/api/offer?search=" + searchText + "&department=" + field1 + "&status=" + field2);
    }
    console.log(searchText);
}

findDepartment = function (id) {
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/offer/department/" + id,
        async: false
    }).done(function (data) {
        renderData("/api/offer?pageIndex=" + pageIndex + "&search=" + searchText + "&field=" + field);
    })
        .fail((error) => {
            console.error(error);
        });
}

function renderDepartment(idDepartment) {
    let idFetchDepartment = idDepartment.substring(11, Number.MAX_VALUE);
    let url = "/api/offer/department/" + idFetchDepartment;
    let fetCandidate = fetch(url)
        .then(response => response.json())
        .then(d => {
            let department = d.department[0].name;
            let htmlDepartment = "";
            htmlDepartment += `<span>${department}</span>`;
            document.getElementById(idDepartment).innerHTML = htmlDepartment;
        }).catch(error => {
            console.log(error)
        });

}

function displayDepartment() {
    document.querySelectorAll(".department").forEach(function (e, i) {
        renderDepartment(e.id);
    })
}
findCandidateOffer = function (id) {
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/offer/candidateDTO/" + id,
        async: false
    }).done(function (data) {
        renderData("/api/offer?pageIndex=" + pageIndex + "&search=" + searchText + "&field=" + field);
    })
        .fail((error) => {
            console.error(error);
        });
}
function renderCandidateOffer(idCandidateOffer){
    let idFetchCandidateOffer=idCandidateOffer.substring(10,Number.MAX_VALUE);
    let url ="/api/offer/candidateDTO/" + idFetchCandidateOffer;
    let fetCandidateOffer=fetch(url)
        .then(response =>response.json())
        .then(c=>{
            let candidateOffer=c.name;
            let htmlCandidateOffer="";
            htmlCandidateOffer += `<span>${candidateOffer}</span>`;
            document.getElementById(idCandidateOffer).innerHTML=htmlCandidateOffer;

        }).catch(error =>{
            console.log(error)
        });
}
function displayCandidateOffer(){
    document.querySelectorAll(".candidate").forEach(function (e,i){
        renderCandidateOffer(e.id);
    });
}
function renderCandidateEmail(idCandidateOffer){
    let idFetchCandidateOffer=idCandidateOffer.substring(15,Number.MAX_VALUE);
    let url ="/api/offer/candidateDTO/" + idFetchCandidateOffer;
    let fetCandidateOffer=fetch(url)
        .then(response =>response.json())
        .then(c=>{
            let candidateOffer=c.email;
            let htmlCandidateOffer="";
            htmlCandidateOffer += `<span>${candidateOffer}</span>`;
            document.getElementById(idCandidateOffer).innerHTML=htmlCandidateOffer;

        }).catch(error =>{
            console.log(error)
        });
}
function displayCandidateEmail(){
    document.querySelectorAll(".candidateEmail").forEach(function (e,i){
        renderCandidateEmail(e.id);
    });
}

findApprovalOffer = function (id) {
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/offer/user/" + id,
        async: false
    }).done(function (data) {
        renderData("/api/offer?pageIndex=" + pageIndex + "&search=" + searchText + "&field=" + field);
    })
        .fail((error) => {
            console.error(error);
        });
}
function renderUser(idUser){
    let idFetchUser=idUser.substring(5,Number.MAX_VALUE);
    let url ="/api/offer/user/" + idFetchUser;
    let fetUser=fetch(url)
        .then(response =>response.json())
        .then(u=>{
            let user=u.name;
            let htmlUser="";
            htmlUser += `<span>${user}</span>`;
            document.getElementById(idUser).innerHTML=htmlUser;

        }).catch(error =>{
            console.log(error)
        });
}

function displayUser(){
    document.querySelectorAll(".user").forEach(function (e,i){
        renderUser(e.id);
    });
}
function showExport() {
    document.querySelector(".export-offer").classList.remove("hidden");
}

function hiddenExport() {
    document.querySelector(".export-offer").classList.add("hidden");
}


renderData("/api/offer")
