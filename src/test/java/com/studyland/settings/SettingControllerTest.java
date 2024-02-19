package com.studyland.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyland.WithAccount;
import com.studyland.account.AccountRepository;
import com.studyland.account.AccountService;
import com.studyland.domain.Account;
import com.studyland.domain.Tag;
import com.studyland.settings.form.TagForm;
import com.studyland.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired AccountService accountService;

    // withAccount 에서 생성한 데이터를 지워줘야한다.
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("/radness")
    @DisplayName(("태그 수정 폼"))
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("radness")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        // 해당 요청은 transaction 에서 처리가 된다.
        mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/add")
                // 요청 안에 본문은 JSON 타입으로 들어온다.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        // 밖에서는 처리가 안된다.
        // 태그가 저장되었는지 확인
        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag); // 태그가 있어야 하고
        // radness 유저가 있을테니 계정 안에 tag가 들어있는지 확인
        Account radness = accountRepository.findByNickname("radness");
        // 가져온 radness 객체의 상태는 persist 가 아닌 detached 상태이다.
        // persist 상태를 유지하려면 SettingsControlTest 에 @Transactional 어노테이션을 추가해야한다.
        // @Transactional 어노테이션을 추가하면 persist 상태가 되고 lazyLoading 을 할 수 있게 된다.
        assertTrue(radness.getTags().contains(newTag));
    }

    @WithAccount("radness")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account account = accountRepository.findByNickname("radness");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(account, newTag);

        assertTrue(account.getTags().contains(newTag)); // 가지고 있는지 확인

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/remove")
                        // 요청 안에 본문은 JSON 타입으로 들어온다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        // account 계정이 tag를 가지고 있지 않는지 확인
        assertFalse(account.getTags().contains(newTag));
    }

    // withAccount 가 없으면 동작하지 않는다.
    @WithAccount("radness")
    @DisplayName("프로필 수정하기 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("radness")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(MockMvcRequestBuilders.post(SettingController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("radness");
        assertEquals(bio, account.getBio());
    }

    @WithAccount("radness")
    @DisplayName("프로필 수정하기 - 입력값 오류")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.";
        mockMvc.perform(MockMvcRequestBuilders.post(SettingController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("radness");
        assertNull(account.getBio());
    }

    @WithAccount("radness")
    @DisplayName("암호 수정 폼")
    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SettingController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("radness")
    @DisplayName("암호 수정하기 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SettingController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12341234")
                        .param("newPasswordConfirm", "12341234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("radness");
        assertTrue(passwordEncoder.matches("12341234", account.getPassword()));
    }

    @WithAccount("radness")
    @DisplayName("암호 수정하기 - 입력값 오류")
    @Test
    void updatePassword_error() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SettingController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12341234")
                        .param("newPasswordConfirm", "12121212")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

}