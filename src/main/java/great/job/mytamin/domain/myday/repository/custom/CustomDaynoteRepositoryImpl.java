package great.job.mytamin.domain.myday.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import great.job.mytamin.domain.myday.entity.Daynote;
import great.job.mytamin.domain.myday.entity.QDaynote;
import great.job.mytamin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomDaynoteRepositoryImpl implements CustomDaynoteRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QDaynote daynote = QDaynote.daynote;

    @Override
    public List<Daynote> searchDaynoteList(User user) {
        return jpaQueryFactory
                .selectFrom(daynote)
                .where(
                        eqUser(user)
                )
                .orderBy(sorting())
                .fetch();
    }

    // 유저
    private BooleanExpression eqUser(User user) {
        return daynote.user.eq(user);
    }

    // 정렬
    private OrderSpecifier<?> sorting() {
        return new OrderSpecifier<>(Order.DESC, daynote.performedAt); // 최신 순
    }

}
