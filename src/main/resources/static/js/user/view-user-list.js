const contextPath= "";
var field = "";
var totalPage = 0;
var pageIndex = 0;
var pageIndexNext = 0;
var pageIndexPrev = 0;
var searchText = "";
var roles = "";

function renderData(url) {
    let fet = fetch(url)
        .then(response => response.json())
        .then(page => {
            totalPage = page.totalPage;
            pageIndex = page.pageIndex;
            let users = page.data

            let trows = "";

            if (users.length == 0) {
                trows = `<p class="not-found">No User has been found</p>`;
            } else {
                for (let u of users) {
                    trows +=
                        `<tr>
                           <td>${u.username}</td> 
                           <td>${u.email}</td> 
                           <td>${u.phoneNumber}</td> 
                           <td>${u.role.roleName}</td> 
                           <td>${u.status}</td> 
                           <td class="action-role">
                               <a href="${contextPath}/user/view/${u.id}"><i class="fa-solid fa-eye detail"></i></a>                           
                               <a href="${contextPath}/user/edit/${u.id}?prevPage=list""><i class="fa-solid fa-pen-nib edit"></i></a>                                             
                            </td> 
                        </tr>`
                }
            }

            $(document).ready(function () {
                $(".delete").on("click", function (e) {
                    e.preventDefault();
                    let link = $(this);
                    let deleteURL = link.attr("href");
                    $("#confirmText").html("Do you want to delete permanently this user?");
                    $("#confirmModal").modal('show');
                    $("#yesBtn").on("click", function () {
                        window.location.href = deleteURL;
                    })
                })
                $("#confirmModal .modal-footer .btn-secondary, #confirmModal .modal-header .close").on("click", function () {
                    $("#confirmModal").modal('hide');
                });
            })

            document.getElementsByTagName("tbody")[0].innerHTML = trows;

            let paging = "";
            for(let i = 1; i <= page.totalPage; i++) {
                let active = i==page.pageIndex ? "active" : "";
                paging += `<li class="${active}" pageIndex="${i}" onclick="paging(event)" style="width: 30px; height: 30px; color: #5D87FF; text-align: center; line-height: 30px; border: 1px solid #5D87FF; cursor: pointer">${i}</>`
            }

            document.getElementsByClassName("pagination")[0].innerHTML = paging;

            renderAction();
        })
        .catch(error => {
            console.log(error)
        })
}

function paging(event) {
    event.preventDefault();
    let targetElement = event.target;
    if(searchText == "" && field=="") {
        renderData("/api/user?pageIndex=" + targetElement.getAttribute("pageIndex"));
    } else {
        renderData("/api/user?pageIndex=" + targetElement.getAttribute("pageIndex") + "&search=" + searchText + "&field" + field);
    }
}


function next(event) {

    event.preventDefault();
    if (pageIndex >= totalPage){
        pageIndexPrev = pageIndex;
    } else {
        pageIndexPrev = pageIndex+1;
    }

    if (searchText=="" && field=="") {
        renderData("/api/user?pageIndex=" + pageIndexPrev);
    } else {
        renderData("/api/user?pageIndex=" + pageIndexPrev + "&search="+searchText+"&field="+field);
    }

}

function prev(event) {

    event.preventDefault();
    if (pageIndex <= 1){
        pageIndexNext = pageIndex;
    } else {
        pageIndexNext = pageIndex-1;
    }

    if (searchText=="" && field=="") {
        renderData("/api/user?pageIndex=" + pageIndexNext);
    } else {
        renderData("/api/user?pageIndex=" + pageIndexNext+"&search="+searchText+"&field="+field);
    }
}

search = function (event){

    event.preventDefault();
    searchText = document.getElementById("searchInput").value;
    field = document.getElementsByTagName("option");
    field = Array.from(field).filter(x=>x.selected)[0].value;

    if (searchText=="" && field==""){
        renderData("/api/user");
    } else {
        renderData("/api/user?search="+searchText+"&role="+field);
    }

}

function hiddenCreatedMessage() {
    document.querySelector(".creatMessage").classList.add("hidden");
}

renderData("/api/user");
