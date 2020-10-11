<template>
  <v-skeleton-loader v-if="loading" type="card" loading/>
  <div v-else>
    <v-list-item :class="{'elevation-2':!transparent, 'rounded-lg':true,
     'card-wrapper':!noLink,'card-wrapper-noHover':noLink===true,
     'pa-0':true,'m-0':true}"
                 three-line :to="noLink? null : '/movies/'+movie.id">
      <v-list-item-avatar rounded="0"
                          height="370" :width="240"
                          max-height="370" max-width="240"
                          horizontal :class="{'ma-0':true, 'pa-0':true, 'mr-4':true, 'card-avatar':noLink===true}">
        <v-img class="card-img rounded-l-lg" :src="movie.posterUrl" style="height:100%!important;"/>
      </v-list-item-avatar>
      <v-list-item-content class="movie-details">
        <v-list-item-title class="movie-title">{{movie.name}}</v-list-item-title>
        <v-list-item-subtitle class="post-description">{{movie.description}}</v-list-item-subtitle>
        <v-row justify="start">
          <shows-view v-if="showView" :loading="loading" :shows="shows" :mov-id="movie.id"/>
        </v-row>
        <v-list-item-title class="post-author">
          <v-row dense no-gutters justify="space-between">
            <v-col cols="9">
              Directed by {{movie.director}}
            </v-col>
            <v-spacer/>
            <v-col>
              <v-btn icon v-on:click.prevent="openTrailer(movie.id)">
                <v-icon>fas fa-video</v-icon>
              </v-btn>
            </v-col>
          </v-row>

        </v-list-item-title>
      </v-list-item-content>

    </v-list-item>
  </div>

</template>

<script>

  import ShowsView from "../misc/shows-view";
  import LoginForm from "../forms/LoginForm";
  import TrailerDialog from "../misc/TrailerDialog";

  export default {
    name: "MovieCard",
    components: {ShowsView},
    props: {
      movie: {
        id: Number,
        name: String,
        posterUrl: String,
        director: String,
        description: String,
      },
      shows: Array,
      showView: Boolean,
      noLink: Boolean,
      transparent: Boolean,
      loading: Boolean
    },
    methods: {
      async openTrailer(movId){
        let trailerUrl= await this.$repos.movies.trailer(movId);
        console.log(trailerUrl)
        let trailerDialog = await this.$dialog.showAndWait(TrailerDialog,{trailerUrl})
          .then(resp => resp);
        console.log("trailer",trailerDialog)
      }
    }
  }
</script>

<style scoped lang="scss">
  @import url('https://fonts.googleapis.com/css?family=Roboto:400,700');

  $bg: #eedfcc;
  $text: #777;
  $black: #121212;
  $white: #fff;
  $red: #e04f62;
  $border: #ebebeb;
  $shadow: rgba(0, 0, 0, 0.2);

  @mixin transition($args...) {
    transition: $args;
  }

  * {
    box-sizing: border-box;

    &::before, &::after {
      box-sizing: border-box;
    }
  }

  .movie-card {
    display: flex;
    flex-direction: row;
    background: $white;
    box-shadow: 0 0.1875rem 1.5rem $shadow;
    border-radius: 0.375rem;
    overflow: hidden;
  }

  .transparent-card {
    background: transparent;
    box-shadow: none;
    border-radius: 0;
  }

  .card-wrapper {
    background: $white;

    &:hover {
      cursor: pointer;

    }

    &:hover .movie-title {
      @include transition(color 0.3s ease);
      color: $red;
    }

    &:hover .card-img {
      @include transition(opacity 0.3s ease);
      opacity: 0.75;
    }
  }

  .card-avatar {
    transition: width 1s;

    /* &:hover {
       width:240px!important;
       transition: width 1s;
     }*/
  }


  .card-wrapper-noHover {
    &:hover * {
      cursor: auto;
    }

    &:hover {
      background: $white;
    }
  }


  .movie-details {
    // padding: .7rem;
  }

  .movie-title {
    display: inline-block;
    text-transform: uppercase;
    letter-spacing: 0.0625rem;
    padding: 0 0 0.25rem 0;
    border-bottom: 0.125rem solid $border;
    @include transition(color 0.3s ease);
    font-size: 1.125rem;
    line-height: 1.4;
    color: $black;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
  }

  .post-author {
    font-size: 0.875rem;
    line-height: 1;
    margin: 1.125rem 0 0 0;
    padding: 1.125rem 0 0 0;
    border-top: 0.0625rem solid $border;
  }

</style>
