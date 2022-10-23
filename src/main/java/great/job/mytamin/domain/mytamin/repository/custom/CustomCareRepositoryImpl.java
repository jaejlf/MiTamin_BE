package great.job.mytamin.domain.mytamin.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.entity.QCare;
import great.job.mytamin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomCareRepositoryImpl implements CustomCareRepository {

    private final JPAQueryFactory jpaQueryFactory;
//    private final TimeUtil timeUtil;

    QCare care = QCare.care;

    @Override
    public List<Care> searchCareHistory(User user, CareSearchFilter careSearchFilter) {
        return jpaQueryFactory
                .selectFrom(care)
                .where(
                        eqUser(user),
                        eqCategory(careSearchFilter.getCareCategoryCodeList())
//                        betweenqDate(careSearchFilter.getDate())
                )
                .orderBy(sorting())
                .fetch();
    }

    // 유저
    private BooleanExpression eqUser(User user) {
        return care.user.eq(user);
    }

    // 칭찬 카테고리
    private BooleanBuilder eqCategory(List<Integer> careCategoryCodeList) {
        if (careCategoryCodeList == null) return null;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (Integer careCategory : careCategoryCodeList) {
            booleanBuilder.or(care.careCategoryCode.eq(careCategory));
        }
        return booleanBuilder;
    }

//    // 날짜
//    private BooleanExpression betweenqDate(String date) {
//        if (date == null) return null;
//        LocalDateTime target = timeUtil.convertRawToLocalDateTime(date);
//        LocalDateTime start = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), 1, 0, 0);
//        LocalDateTime end = timeUtil.getLastDayOfMonth(target);
//        return care.takeAt.between(start, end);
//    }

    // 정렬
    private OrderSpecifier<?> sorting() {
        return new OrderSpecifier<>(Order.DESC, care.careId); // 최신 순
    }

}
