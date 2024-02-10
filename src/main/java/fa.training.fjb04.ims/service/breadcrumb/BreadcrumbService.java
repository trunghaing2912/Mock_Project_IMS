package fa.training.fjb04.ims.service.breadcrumb;

import fa.training.fjb04.ims.entity.Breadcrumb.Breadcrumb;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BreadcrumbService {

    public Breadcrumb getBreadcrumbJobList() {
        return new Breadcrumb("Job List", "/job/list");
    }

    public Breadcrumb getBreadcrumbJobAdd() {
        return new Breadcrumb("Create Job", "/job/add");
    }

    public Breadcrumb getBreadcrumbJobDetail() {
        return new Breadcrumb("Job Details", "/job/view/");
    }

    public Breadcrumb getBreadcrumbJobEdit() {
        return new Breadcrumb("Edit Job", "/job/edit/");
    }

    public Breadcrumb getBreadcrumbUserList() {
        return new Breadcrumb("User List", "/user/list");
    }

    public Breadcrumb getBreadcrumbUserAdd() {
        return new Breadcrumb("Create User", "/user/add");
    }

    public Breadcrumb getBreadcrumbUserDetail() {
        return new Breadcrumb("User Details", "/user/view/");
    }

    public Breadcrumb getBreadcrumbUserEdit() {
        return new Breadcrumb("Edit User", "/user/edit/");
    }

    public Breadcrumb getBreadcrumbCandidateList() {
        return new Breadcrumb("Candidate List", "/candidate/list");
    }

    public Breadcrumb getBreadcrumbCandidateAdd() {
        return new Breadcrumb("Create Candidate", "/candidate/add");
    }

    public Breadcrumb getBreadcrumbCandidateDetail() {
        return new Breadcrumb("Candidate Details", "/candidate/view/");
    }

    public Breadcrumb getBreadcrumbCandidateEdit() {
        return new Breadcrumb("Edit Candidate", "/candidate/edit/");
    }

    public Breadcrumb getBreadcrumbInterviewList() {
        return new Breadcrumb("Interview List", "/interview/list");
    }

    public Breadcrumb getBreadcrumbInterviewAdd() {
        return new Breadcrumb("Create Interview", "/interview/add");
    }

    public Breadcrumb getBreadcrumbInterviewDetail() {
        return new Breadcrumb("Interview Details", "/interview/view/");
    }

    public Breadcrumb getBreadcrumbInterviewEdit() {
        return new Breadcrumb("Edit Interview", "/interview/edit/");
    }

    public Breadcrumb getBreadcrumbInterviewSubmit() {
        return new Breadcrumb("Submit Interview", "/interview/submit/");
    }

    public Breadcrumb getBreadcrumbOfferList() {
        return new Breadcrumb("Offer List", "/offer/list");
    }

    public Breadcrumb getBreadcrumbOfferAdd() {
        return new Breadcrumb("Create Offer", "/offer/add");
    }

    public Breadcrumb getBreadcrumbOfferDetail() {
        return new Breadcrumb("Offer Details", "/offer/view/");
    }

    public Breadcrumb getBreadcrumbOfferEdit() {
        return new Breadcrumb("Edit Offer", "/offer/edit/");
    }

}
