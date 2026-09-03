package com.example.seed.dto;

public class PolicyProposalResponse {

    private final String proposalId;
    private final String policyType;
    private final String title;
    private final String description;
    private final String purpose;

    public PolicyProposalResponse(
            String proposalId,
            String policyType,
            String title,
            String description,
            String purpose
    ) {
        this.proposalId = proposalId;
        this.policyType = policyType;
        this.title = title;
        this.description = description;
        this.purpose = purpose;
    }

    public String getProposalId() {
        return proposalId;
    }

    public String getPolicyType() {
        return policyType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPurpose() {
        return purpose;
    }
}