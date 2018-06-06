package gr.uoa.di.madgik.publicfood.security.controller;

import gr.uoa.di.madgik.publicfood.model.*;
import gr.uoa.di.madgik.publicfood.security.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserHasChildrenRepository userHasChildrenRepository;
    @Autowired
    private ChildRepository childRepository;
    @Autowired
    ChildrenHasAllergyRepository childrenHasAllergyRepository;
    @Autowired
    WeeklyQuestionnaireRepository weeklyQuestionnaireRepository;
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/user/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @RequestParam int userId, @RequestParam String domain) {

        List<UserHasChildrenEntity> userChildrenIds = userHasChildrenRepository.findByUserIdAndDomainName(userId, domain);
        List<ChildrenEntity> childrenEntities = new ArrayList<ChildrenEntity>();

        List<WeeklyQuestionnaireEntity> weeklyQuestionnaireEntities = weeklyQuestionnaireRepository.findByUserIdAndDomain(String.valueOf(userId), domain);

        for(WeeklyQuestionnaireEntity weeklyQuestionnaireEntity : weeklyQuestionnaireEntities){

            weeklyQuestionnaireRepository.delete(weeklyQuestionnaireEntity);
        }



        for (UserHasChildrenEntity userHasChildrenEntity : userChildrenIds) {
            userHasChildrenRepository.delete(userHasChildrenEntity);
            ChildrenEntity child = childRepository.findByIdchildrenAndDomainName(userHasChildrenEntity.getChildrenIdchildren(), domain);
            List<ChildrenHasAllergyEntity> childAllergens = childrenHasAllergyRepository.findByChildrenIdchildrenAndDomainName(child.getIdchildren(), domain);
            for(ChildrenHasAllergyEntity childrenHasAllergyEntity : childAllergens){
                childrenHasAllergyRepository.delete(childrenHasAllergyEntity);
            }
            //            child.setAllergyEntities(childAllergens);
            //childrenEntities.add(child);
            childRepository.delete(child);
        }

        UserEntity user = userRepository.findByIdAndDomainName(userId, domain);
        userRepository.delete(user);

        return new ResponseEntity<>( HttpStatus.OK);

    }

    @RequestMapping(value = "/user/getChildren", method = RequestMethod.GET)
    public ResponseEntity<?> getChildren(HttpServletRequest request, @RequestParam int userId, @RequestParam String domain) {

        List<UserHasChildrenEntity> userChildrenIds = userHasChildrenRepository.findByUserIdAndDomainName(userId, domain);
        List<ChildrenEntity> childrenEntities = new ArrayList<ChildrenEntity>();

        for (UserHasChildrenEntity userHasChildrenEntity : userChildrenIds) {
            ChildrenEntity child = childRepository.findByIdchildrenAndDomainName(userHasChildrenEntity.getChildrenIdchildren(), domain);
//            List<ChildrenHasAllergyEntity> childAllergens = childrenHasAllergyRepository.findByChildrenIdchildrenAndDomainName(child.getIdchildren(), domain);
//            child.setAllergyEntities(childAllergens);
            childrenEntities.add(child);
        }

        return new ResponseEntity<List<ChildrenEntity>>(childrenEntities, HttpStatus.OK);

    }
}
