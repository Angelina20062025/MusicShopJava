package group.lab6.lab6.model;

import java.time.LocalDateTime;

//Физическое лицо-продавец Б/У пластинки
public class SellerPerson {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public SellerPerson() {}

    public SellerPerson(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public SellerPerson(Long personId, String firstName, String lastName,
                        String patronymic, String phone, LocalDateTime createdAt,
                        LocalDateTime updatedAt, Boolean isDeleted) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(lastName).append(" ").append(firstName);
        if (patronymic != null && !patronymic.isEmpty()) {
            sb.append(" ").append(patronymic);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getFullName() + " (" + phone + ")";
    }
}