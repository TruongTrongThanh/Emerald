package org.emerald.comicapi.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.emerald.comicapi.model.form.UserForm;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Document(collection = "users")
@JsonInclude(Include.NON_EMPTY)
public class User implements UserDetails {

    private @Getter @Id ObjectId id;
    private @Setter String username;
    private @Setter String password;
    private @Getter @Setter String email;
    private @Getter @Setter String avatarUrl;
    private @Setter boolean isAccountNonExpired = true;
    private @Setter boolean isAccountNonLocked = true;
    private @Setter boolean isCredentialsNonExpired = true;
    private @Setter boolean isEnabled = true;
    private @Setter List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList();
    private @Getter @CreatedDate LocalDateTime createdAt;
    private @Getter @LastModifiedDate LocalDateTime modifiedAt;

    public User(){}
    public User(UserForm userForm) {
        username = userForm.getUsername();
        password = userForm.getPassword();
        email = userForm.getEmail();
    }

    public void hideInformation() {
        password = null;
        email = null;
        avatarUrl = null;
        authorities = null;
        createdAt = null;
        modifiedAt = null;
    }

    public void addRole(String role) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
    public void addAuthority(String authority) {
        authorities.add(new SimpleGrantedAuthority(authority));
    }

    @JsonIgnore
    public String getIdHexString() {
        return id == null ? null : id.toHexString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
