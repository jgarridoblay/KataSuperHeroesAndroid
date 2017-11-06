/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import com.karumi.katasuperheroes.ui.view.SuperHeroDetailActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.getIdlingResources;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.karumi.katasuperheroes.matchers.ToolbarMatcher.onToolbarWithTitle;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class SuperHeroDetailActivityTests {

  private static final int NUMBER_OF_SUPER_HEROES = 11;
  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public ActivityTestRule<SuperHeroDetailActivity> activityRule =
      new ActivityTestRule<>(SuperHeroDetailActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @After public void tearDown() {
    List<IdlingResource> idlingResources = getIdlingResources();
    for (IdlingResource resource : idlingResources) {
      unregisterIdlingResources(resource);
    }
  }

  @Test public void showsSuperHeroNameAsToolbarTitle() {
    SuperHero superHero = givenThereIsASuperHero();

    startActivity(superHero);

    onToolbarWithTitle(superHero.getName()).check(matches(isDisplayed()));
  }

  @Test public void hidesProgressBarOnSuperHeroLoaded() {
    SuperHero superHero = givenThereIsASuperHero();

    startActivity(superHero);

    onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
  }

  @Test public void showsSuperHeroName() {
    SuperHero superHero = givenThereIsASuperHero();

    startActivity(superHero);
    scrollToView(R.id.tv_super_hero_name);

    onView(allOf(withId(R.id.tv_super_hero_name), withText(superHero.getName()))).check(
        matches(isDisplayed()));
  }

  @Test public void showsSuperHeroDescription() {
    SuperHero superHero = givenThereIsASuperHero();

    startActivity(superHero);
    scrollToView(R.id.tv_super_hero_description);

    onView(withText(superHero.getDescription())).check(matches(isDisplayed()));
  }

  @Test public void doesNotShowAvengersBadgeIfSuperHeroIsNotPartOfTheAvengersTeam() {
    SuperHero superHero = givenThereIsASuperHero(false);

    startActivity(superHero);

    onView(withId(R.id.iv_avengers_badge)).check(matches(not(isDisplayed())));
  }

  @Test public void showsAvengersBadgeIfSuperHeroIsPartOfTheAvengersTeam() {
    SuperHero superHero = givenAnAvenger();

    startActivity(superHero);

    onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()));
  }

  @Test public void showHeroIsNotAvenger(){
    int heroIndex = 2;
    List<SuperHero> superHeroes = newListOfHeroesNotAvengers(NUMBER_OF_SUPER_HEROES);
    startActivity();
    final SuperHero superHero = superHeroes.get(heroIndex);

    onView(allOf(withId(R.id.recycler_view), hasDescendant(withText(superHero.getName())))).check(matches(hasDescendant(Matchers.allOf(withId(R.id.iv_avengers_badge),
            withEffectiveVisibility(ViewMatchers.Visibility.GONE)))));
  }
  private List<SuperHero> newListOfHeroesNotAvengers(int numberOfHeroes){
    return createSuperHeroeList(numberOfHeroes, false);
  }

  private List<SuperHero> createSuperHeroeList(int numberOfHeroes, boolean avenger){
    List<SuperHero> superHeroesList = new LinkedList<>();
    for (int i = 0; i < numberOfHeroes; i++) {
      String superHeroeName = "SuperHeroName" + i;
      String icono = "https://www.google.es/imgres?imgurl=http%3A%2F%2Fblogs.deia.com%2Fdiseno-a-sorbos%2Ffiles%2F2015%2F04%2FCalico-450.jpg&imgrefurl=http%3A%2F%2Fblogs.deia.com%2Fdiseno-a-sorbos%2F2015%2F04%2F14%2Fadios-a-calico-electronico%2F&docid=QSeiISHtJRXm_M&tbnid=A4DvBm6NRqHFqM%3A&vet=10ahUKEwi5wdKliKLXAhUCwxQKHYfFCd4QMwgxKAswCw..i&w=450&h=252&client=firefox-b-ab&bih=1175&biw=1093&q=calicoelectronico&ved=0ahUKEwi5wdKliKLXAhUCwxQKHYfFCd4QMwgxKAswCw&iact=mrc&uact=8";
      SuperHero superHero = new SuperHero(superHeroeName, icono,
              avenger, "SuperDescription number " +i);
      superHeroesList.add(superHero);
      when(repository.getByName(superHeroeName)).thenReturn(superHero);
    }
    when(repository.getAll()).thenReturn(superHeroesList);
    return superHeroesList;
  }
  private SuperHeroDetailActivity startActivity() {
    return activityRule.launchActivity(null);
  }

  private SuperHero givenThereIsASuperHero() {
    return givenThereIsASuperHero(false);
  }

  private SuperHero givenAnAvenger() {
    return givenThereIsASuperHero(true);
  }

  private SuperHero givenThereIsASuperHero(boolean isAvenger) {
    String superHeroName = "SuperHero";
    String superHeroPhoto = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg";
    String superHeroDescription = "Super Hero Description";
    SuperHero superHero =
        new SuperHero(superHeroName, superHeroPhoto, isAvenger, superHeroDescription);
    when(repository.getByName(superHeroName)).thenReturn(superHero);
    return superHero;
  }

  private SuperHeroDetailActivity startActivity(SuperHero superHero) {
    Intent intent = new Intent();
    intent.putExtra("super_hero_name_key", superHero.getName());
    return activityRule.launchActivity(intent);
  }

  private void scrollToView(int viewId) {
    onView(withId(viewId)).perform(scrollTo());
  }
}
