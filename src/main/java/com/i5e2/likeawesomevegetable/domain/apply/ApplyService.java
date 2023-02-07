package com.i5e2.likeawesomevegetable.domain.apply;

import com.i5e2.likeawesomevegetable.domain.apply.dto.ApplyRequest;
import com.i5e2.likeawesomevegetable.domain.apply.dto.ApplyResponse;
import com.i5e2.likeawesomevegetable.domain.apply.exception.ApplyErrorCode;
import com.i5e2.likeawesomevegetable.domain.apply.exception.ApplyException;
import com.i5e2.likeawesomevegetable.domain.market.CompanyBuying;
import com.i5e2.likeawesomevegetable.domain.user.FarmUser;
import com.i5e2.likeawesomevegetable.domain.user.User;
import com.i5e2.likeawesomevegetable.repository.ApplyJpaRepository;
import com.i5e2.likeawesomevegetable.repository.CompanyBuyingJpaRepository;
import com.i5e2.likeawesomevegetable.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyJpaRepository applyJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CompanyBuyingJpaRepository companyBuyingJpaRepository;
    private final String SMS_USER_ID = "SMS_USER_ID";

    // 모집 참여 조회
    public Page<ApplyResponse> list(Long companyBuyingId, Pageable pageable) {

        return applyJpaRepository.findAllByCompanyBuyingId(companyBuyingId, pageable).map(ApplyResponse::fromEntity);
    }

    // 모집 참여 신청하기
    public ApplyResponse apply(ApplyRequest request, Long companyBuyingId, String userEmail, HttpSession session) {

        // 로그인한 사용자인지 확인
        User user = userJpaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApplyException(ApplyErrorCode.INVALID_PERMISSION, ApplyErrorCode.INVALID_PERMISSION.getMessage()));

        // 모집 게시글이 있는지 확인
        CompanyBuying companyBuying = companyBuyingJpaRepository.findById(companyBuyingId)
                .orElseThrow(() -> new ApplyException(ApplyErrorCode.POST_NOT_FOUND, ApplyErrorCode.POST_NOT_FOUND.getMessage()));

        // 신청자가 농가 사용자인지 확인
        Optional<FarmUser> farmUser = Optional.ofNullable(user.getFarmUser());

        if (farmUser.isEmpty()) {
            throw new ApplyException(ApplyErrorCode.NOT_FARM_USER, ApplyErrorCode.NOT_FARM_USER.getMessage());
        }

        // 세션 확인
        Optional.ofNullable(session.getAttribute(SMS_USER_ID))
                .orElseThrow(() -> new ApplyException(ApplyErrorCode.INVALID_PERMISSION, ApplyErrorCode.INVALID_PERMISSION.getMessage()));

        Apply savedApply = applyJpaRepository
                .save(request.toEntity(request.getApplyQuantity(), companyBuying, user));

        // 참여 고유번호 생성(APPLY-날짜-게시글번호-신청ID)
        String applyNumber = "APPLY-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "-" +
                companyBuyingId + "-" + savedApply.getId();

        savedApply.setApplyNumber(applyNumber);

        session.removeAttribute(SMS_USER_ID);
        return ApplyResponse.fromEntity(savedApply);
    }
}