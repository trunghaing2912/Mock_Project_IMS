const contextPath = "";
var roles="";
var searchText = "";
var field = "";
var totalPage = 0;
var pageIndex = 0;
var pageIndexNext = 0;
var pageIndexPrev = 0;

function renderData(url) {
    let fet = fetch(url)
        .then(response => response.json())
        .then(page => {
            totalPage = page.totalPage;
            pageIndex = page.pageIndex;
            roles=page.role;
            let candidates = page.data;
            let trows = "";
            if (candidates.length === 0) {
                trows = `<p class="not-found">No item matches with your search data. Please try
again!</p>`
            } else {
                for (let candidate of candidates) {
                    trows += `
                            <tr>
                              <td>${candidate.fullName}</td>
                              <td>${candidate.email}</td>
                              <td>${candidate.phone}</td>                              
                              <td id="${candidate.id}" class="positions"><span>position_template</span></td>
                              <td id="recruiter-${candidate.id}" class="recruiters"><span>recruiter_template</span></td>
                              <td>${candidate.candidateStatus.statusName}</td>
                              <td class="action-parent">
                                  <a href="${contextPath}/candidate/view/${candidate.id}"><i class="fa-solid fa-eye"></i></a>
                                  <a href="${contextPath}/candidate/edit/${candidate.id}?prevPage=list"><i class="fa-solid fa-pen-nib edit-candidate"></i></a>
                                  <a href="${contextPath}/candidate/delete/${candidate.id}" class="delete"><i class="fa-solid fa-trash-can delete-candidate"></i></a>
                              </td>
                            </tr>`
                }

            }
            //delete
            $(document).ready(function() {
                $(".delete").on("click", function(e) {
                    e.preventDefault();
                    let link = $(this);
                    let deleteUrl = link.attr("href");
                    $("#modal-title").html("Delete confirmation")
                    $("#confirmText").html("Do you want to delete this candidate?");
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
            document.getElementsByTagName("tbody")[0].innerHTML=trows;


            let paging = "";
            for (let i=1; i<= page.totalPage; i++) {
                let active = i==page.pageIndex ? "active" : "";
                paging += `<li class="${active}" pageIndex="${i}" onclick="paging(event)" style="width: 30px; height: 30px; color: #5D87FF; text-align: center; line-height: 30px; border: 1px solid #5D87FF; cursor: pointer">${i}</li>`;
            }

            document.getElementsByClassName("pagination")[0].innerHTML = paging;
            displayPosition();
            displayRecruiter();
            renderAction();
        })
        .catch(error => {
            console.log(error)
        });
}
function paging(event){

    event.preventDefault();
    let targetElement = event.target;
    if (searchText===""&&field==="") {

        renderData("/api/candidates?pageIndex=" + targetElement.getAttribute("pageIndex"));
    } else {
        renderData("/api/candidates?pageIndex=" + targetElement.getAttribute("pageIndex")+"&search="+searchText+"&field="+field);
    }
}
function next(event) {

    event.preventDefault();
    if (pageIndex >= totalPage){
        pageIndexPrev = pageIndex;
    } else {
        pageIndexPrev = pageIndex+1;
    }

    if (searchText===""&&field==="") {
        renderData("/api/candidates?pageIndex=" + pageIndexPrev);
    } else {
        renderData("/api/candidates?pageIndex=" + pageIndexPrev + "&search="+searchText+"&field="+field);
    }

}
function prev(event) {

    event.preventDefault();
    if (pageIndex <= 1){
        pageIndexNext = pageIndex;
    } else {
        pageIndexNext = pageIndex-1;
    }

    if (searchText===""&&field==="") {
        renderData("/api/candidates?pageIndex=" + pageIndexNext);
    } else {
        renderData("/api/candidates?pageIndex=" + pageIndexNext+"&search="+searchText+"&field="+field);
    }
}
search = function (event){

    event.preventDefault();
    searchText = document.getElementById("searchInput").value;
    field = document.getElementsByTagName("option");
    field = Array.from(field).filter(x=>x.selected)[0].value;


    if (searchText===""&&field===""){
        renderData("/api/candidates");
    } else {
        renderData("/api/candidates?search="+searchText+"&field="+field);
    }

}
findPosition = function (id){
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/candidates/position/" + id,
        async:false
    }).done(function (data){
        renderData("/api/candidates?pageIndex=" + pageIndex+"&search="+searchText+"&field="+field);
    })
        .fail((error) => {
            console.error(error);
        });
}
function renderPosition(idPosition){
    let url = "/api/candidates/position/" +idPosition;
    let fetPosition = fetch(url)
        .then(response=>response.json())
        .then(p=>{
            let position = p.positionName;
            let htmlPosition = "";
            htmlPosition+=`<span>${position}</span>`;
            document.getElementById(idPosition).innerHTML=htmlPosition;
        }).catch(error => {
            console.log(error)
        });

}
function displayPosition(){
    document.querySelectorAll(".positions").forEach(function (e,i){
        renderPosition(e.id);
    })
}

findRecruiter = function (id){
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/api/candidates/recruiter/" + id,
        async:false
    }).done(function (data){
        renderData("/api/candidates?pageIndex=" + pageIndex+"&search="+searchText+"&field="+field);
    })
        .fail((error) => {
            console.error(error);
        });
}

function renderRecruiter(idRecruiter){
    let idFetchRecruiter = idRecruiter.substring(10, Number.MAX_VALUE);
    let url = "/api/candidates/recruiter/" +idFetchRecruiter;
    let fetRecruiter = fetch(url)
        .then(response=>response.json())
        .then(p=>{
            let recruiter = p.recruiterName;
            let htmlRecruiter = "";
            htmlRecruiter+=`<span>${recruiter}</span>`;
            document.getElementById(idRecruiter).innerHTML=htmlRecruiter;
        }).catch(error => {
            console.log(error)
        });

}
function displayRecruiter(){
    document.querySelectorAll(".recruiters").forEach(function (e,i){
        renderRecruiter(e.id);
    })
}
function renderAction() {
    if (roles === "[ROLE_INTERVIEWER]") {
        document.querySelectorAll(".edit-candidate").forEach((e, i) => {
            e.classList.add("hidden")
        });
        document.querySelectorAll(".delete-candidate").forEach((e, i) => {
            e.classList.add("hidden")
        });
    }
}

renderData("/api/candidates");