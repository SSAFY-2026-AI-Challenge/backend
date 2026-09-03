package com.example.seed.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PolicySimulationRequest {

    @NotBlank
    private String proposalId;

    @Valid
    @NotNull
    private Parameters parameters;

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public static class Parameters {

        @NotNull
        private BigDecimal incomeTaxRate;

        public BigDecimal getIncomeTaxRate() {
            return incomeTaxRate;
        }

        public void setIncomeTaxRate(BigDecimal incomeTaxRate) {
            this.incomeTaxRate = incomeTaxRate;
        }
    }
}