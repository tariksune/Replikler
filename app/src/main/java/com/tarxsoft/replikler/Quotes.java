package com.tarxsoft.replikler;

public class Quotes {

    private Integer id;
    private String quoteId;
    private String quoteName;
    private String quoteText;
    private String quoteLink;

    public Quotes(){

    }
    public Quotes(Integer id, String quoteId, String quoteName, String quoteText, String quoteLink){
        this.id = id;
        this.quoteId = quoteId;
        this.quoteName = quoteName;
        this.quoteText = quoteText;
        this.quoteLink = quoteLink;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteName() {
        return quoteName;
    }

    public void setQuoteName(String quoteName) {
        this.quoteName = quoteName;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteLink() {
        return quoteLink;
    }

    public void setQuoteLink(String quoteLink) {
        this.quoteLink = quoteLink;
    }
}
