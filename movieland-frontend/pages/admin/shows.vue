<template>
  <v-container>
    <v-row justify="center">
      <h4>Movie Shows</h4>
    </v-row>
    <v-row justify="center">
      <v-col cols="12" md="10">
        <v-data-table :loading="loading" :headers="headers" :items="movieInfos" v-model="selected"
                      item-key="movId" class="elevation-1" sort-desc sort-by="date"
                      :search="search" :items-per-page="10" show-select
        >
          <template v-slot:top>
            <template>
              <v-toolbar flat class="pt-2">
                <v-menu
                  transition="scale-transition"
                  :close-on-content-click="true"
                  min-width="290px" class="mt-4"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-combobox hide-details
                                v-model="dateRangeText"
                                label="Date Range"
                                prepend-icon="mdi-calendar"
                                readonly
                                v-bind="attrs"
                                v-on="on"
                    ></v-combobox>
                  </template>
                  <v-date-picker
                    v-model="dates"
                    no-title
                    scrollable
                  >
                  </v-date-picker>
                </v-menu>
                <v-btn @click="findShows" color="success" class="mr-2">
                  <v-icon>fas fa-search</v-icon>
                </v-btn>
                <v-btn @click="selectToday" color="secondary">
                  Today
                </v-btn>

              </v-toolbar>
            </template>

          </template>

          <template v-slot:item.posterUrl="{ item }">
            <v-img :src="item.posterUrl" max-width="80"/>
          </template>

          <template v-slot:item.details="{ item }">
            {{item.name}}<br/>
            ({{item.date | formatDate}})
          </template>

          <template v-slot:item.shows="{ item }">
            <v-row justify="center">
              <v-simple-table dense style="background-color: transparent" >
                <template v-slot:default>
                  <!--    <thead>
                      <tr>
                        <th v-for="(show,idx) of item.shows" style="width: 30px!important;">
                          {{key | formatDateName }}
                        </th>
                      </tr>
                      </thead>-->
                  <tbody>
                  <tr v-for="(show,idx) of item.shows" >
                    <td style="border-bottom: none!important;">
                      <v-row dense justify="start" align="start">
                        <div class="show-item" @click="$router.push(`/movies/${item.id}/shows/${show.id}`)">
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
            </v-row>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn fab x-small color="error" class="mr-2" elevation="0">
              <v-icon>fas fa-trash</v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  import moment from 'moment'

  export default {
    name: "AdminShows",
    data() {
      return {
        dates: moment().format('YYYY-MM-DD'),
        movieInfos: [],
        headers: [
          {
            value: 'posterUrl',
            text: 'Poster',
            sortable: false,
            width: '80px',
            align: 'center'
          },
          {
            value: 'details',
            text: 'Details',
            align: 'center'
          },
          {
            value: 'shows',
            text: 'Shows',
            width: '30%',
            sortable: false,
            align: 'center'
          },
          {
            text: 'Actions',
            value: 'actions',
            sortable: false,
            align: 'center'
          },
        ],
        selected: [],
        loading: true,
        search: ""
      }
    },
    async fetch() {

    },
    mounted() {
      this.findShows();
    },
    methods: {
      async findShows() {
        this.loading = true;
        console.log("date",this.dates)
        this.movieInfos = await this.$repos.shows.showInfos(this.dates);
        console.log("infos", this.movieInfos)
        this.loading = false;
      },
      selectToday(){
        this.dates=moment().format('YYYY-MM-DD');
        this.findShows();
      }
    },
    computed: {
      dateRangeText() {
        return this.dates
      },
    },
  }
</script>

<style scoped lang="scss">


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

  .show-time {
    background-color: var(--v-primary-lighten2);
    color: var(--v-primary-lighten4);

    &:hover {
      color: var(--v-primary-lighten5);
    }

  }
</style>
