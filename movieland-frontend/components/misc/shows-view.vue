<template>
  <!--    <v-list>
        <v-list-item v-for="show of shows">
          <v-list-item-action>
            <a :href="`/movies/${movId}/shows/${show.showId}`">
              {{show.date | formatDateTime}}
            </a>
          </v-list-item-action>
        </v-list-item>
      </v-list>-->
  <div style="overflow: hidden" >
    <v-skeleton-loader v-if="loading" type="table" />
    <v-simple-table v-else dense class="shows-table">
      <template v-slot:default>
        <thead>
        <tr>
          <th v-for="(day,key) of days" style="width: 30px!important;">
            {{key | formatDateName }}
          </th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td v-for="(day) of days">
            <v-row dense v-for="show of day" justify="start" align="start">
              <div class="show-item" @click="goToShow(show)">
                <p>
                  {{show.date | formatTime}}
                </p>
              </div>
            </v-row>
          </td>
        </tr>
        </tbody>
      </template>
    </v-simple-table>
  </div>

</template>

<script>
  import Utils from "../../service/Utils";

  export default {
    name: "ShowsView",
    props: {
      shows: Array,
      movId: Number,
      loading: Boolean
    },
    computed: {
      days: {
        get() {
          /*          let days=Stream.of(this.shows);
                    days.groupBy((show)=>{show.date.getDate()})*/
          let days = Utils.groupByDay(this.shows);
          console.log("DAYS", days)
          return days;
        }
      }
    },
    methods: {
      goToShow(show) {
        this.$router.push(`/movies/${this.movId}/shows/${show.id}`);
      }
    }
  }
</script>

<style scoped lang="scss">
  @import '~vuetify/src/styles/styles.sass';

  .v-data-table
  tbody
  tr:hover:not(.v-data-table__expanded__content) {
    background: transparent !important;
  }

  .show-item {
    width: 6em;
    height: 2em;
    text-align: center;
    background-color: rgba(199, 57, 57, 0.3);
    margin-bottom: 0.1rem;

    &:hover {
      //border-radius: 10%;
      cursor: pointer;
      background-color: rgba(199, 57, 57, 0.6);
    }

    p {
      color: darken(red, 30);
    }
  }

  .shows-table {
    /*   width: 40vw!important;*/
  }
</style>
