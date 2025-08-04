<template>
  <div class="container_file">
    <header>
      <div class="logo">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 512" xml:space="preserve" width="160"><g id="图层_1"><path fill="#004299" d="M887.9 216.4h-47.4v-11.8h70v11.8h-5.3V252h-17.3v-35.6zm25.2 41.6h-72.6v11.8h47.4v49.4h-21l7.4 12.5h30.8v-61.9h7.9V258zm-37.3-35.8c3.7 0 6.7 3 6.7 6.7V252H846v-29.9h29.8zm-6.7 11c0-1.4-1.1-2.5-2.5-2.5h-7v12.9h9.5v-10.4zm13.5 71.5h-36.5v-29.9h29.8c3.7 0 6.7 3 6.7 6.7v23.2zm-13.5-18.8c0-1.4-1.1-2.5-2.5-2.5h-7v12.9h9.5v-10.4zm54.6-37.5 9.7-23.2h21.2c-.9 5.9-3.3 16.1-5.1 23.2H963c2.7-8.8 4.2-15.6 6.1-23.4 1.5-5.9-3-11.7-9.1-11.7h-23.1l5-12.5h-15.3l-18.2 47.5h15.3zm30.5 18.5h-10c.8-9.1 2.1-17.4 3-26.8h-15.7c-5.2 41.9-11.1 62.8-20.1 91.5h16.9c3.8-9.6 8.4-27 12.8-46.5 6 17.8 10.2 33.2 15.4 46.5H974c-9.4-24.7-13.8-41.2-19.8-64.7zm-442.4-33.3v50.2h-55.5v47.8h-19.7v-47.8h-55.5v-62.4h55.5v-20.5h19.7v20.5h43.4c6.7.1 12.1 5.5 12.1 12.2zm-75.2-.4h-35.8v39h35.8v-39zm55.5 0h-35.8v39h35.8v-39zm228-10.2-12.8-22.1h-19.6l11.8 22.1h20.6zm86-18.7h-88.2l6.9 11.8h74.3v115.5h19.3v-115c0-6.8-5.5-12.3-12.3-12.3zM693 331.7h19.7V228.2H693v103.5zm25.8-103.5h72.9v9.9H783v58.2l8.7-.6v11.8l-8.7.6v18.2h-16.7v-17.1l-47.5 3.1v-11.8l8.7-.6V238h-8.7v-9.8zm25.4 24.1h22.1v-14.2h-22.1v14.2zm0 23.3h22.1v-13.5h-22.1v13.5zm0 23.3 22.1-1.5v-12h-22.1v13.5zm-161.7-45.8 4.1 57.7h13.8l-5.8-57.7h-12.1zm-47 57.8h13.8l6.5-57.7h-12.2l-8.1 57.7zm48.4-95.6c3.9-.4 7.4-1 10.5-2v-12.4c-6.7 2.1-14.9 3.2-24.4 3.2h-26.8v11.7h14.7c8.9 0 20.5-.1 26-.5zm48.3 24.8-16.1-30h-14.8l14.8 30h16.1zm0 34.3-16.1-30h-14.8l14.8 30h16.1zM594.4 231h-15.5v-10.9h-19.7V231h-16.1v11.8h16.1v88.8h19.7v-88.8h15.5V231zm63-30.1h-19.7v84.6l-31.2 2.3v11.8l31.2-2.3v34.3h19.7v-35.7l10.7-.8v-11.8l-10.7.8v-83.2z"></path><path fill="#eb6700" d="m99.6 236.9 42-63.3c1.8-2.8-.1-6.5-3.5-6.5h-84c-3.3 0-5.3 3.7-3.5 6.5l42 63.3c1.7 2.4 5.4 2.4 7 0z"></path><path fill="#004299" d="M291.3 167.1c-1.4 0-2.7.7-3.5 1.9 0 0-53.5 81-71.6 107.9-4.7 7-12.6 4.9-15.8 0l-24.6-37.4c-2.7-4.2.2-9.7 5.2-9.7h49.4c8.9 0 14.3-10 9.3-17.4L210.9 169c-1.6-2.5-5.3-2.5-6.9 0l-76.7 115.5c-.9 1.4-.9 3.2 0 4.6l35.8 54c1.6 2.5 5.3 2.5 6.9 0l30.3-45.7c3.3-5 12.5-5.7 16.2 0l28 45.7c1.6 2.5 5.3 2.5 6.9 0L364 173.6c1.8-2.8-.1-6.5-3.5-6.5h-69.2z"></path></g></svg>
      </div>
      <span class="line">|</span>
      <CoolYayiLogoNew  size="120" color="#272a31"/>
    </header>
    <div class="file">
      <component :is="componentName" class="markdown-body" />
    </div>
  </div>
</template>
<script lang="ts" setup>
// @ts-ignore
import ServiceAgreement from './components/service-agreement.md'
// @ts-ignore
import PrivacyPolicy from './components/privacy-policy.md'
import { useRoute } from 'vue-router';
import { ref } from 'vue';
// import 'github-markdown-css'

const componentName = ref()
const route = useRoute()
const pathName = route.name
componentName.value = pathName === 'serviceAgreement' ? ServiceAgreement : PrivacyPolicy
</script>
<style lang="scss" scoped>
.container_file {
  width: 1200px;
  margin: auto;
  overflow-x: auto;
  header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-left: 60px;
    .logo {
      min-width: 160px;
    }
    .line {
      display: inline-block;
      margin: 0 10px 0 5px;
      color: #999;
    }

  }

  .file {
    width: 100%;
    min-width: 375px;
    margin: 0 auto;
    padding: 50px 80px;
    background: #FFFFFF;
    box-shadow: 0px 10px 20px 0px rgba(30, 66, 175, 0.06);
    .markdown-body {
      background-color: #FFF!important;
      color: #24292f!important;
      :deep(h2) {
        text-align: center;
      }
      
    }
  }

  @media screen and (max-width: 1200px) {
    .file {
      padding: 40px 60px;
    }
  }
}

.st2 {
  fill: #7d7d7d
}

.st3 {
  fill: #001cb4
}

.st4 {
  fill: #0059ff
}

.st5 {
  fill: #00b8ff
}

.st6 {
  fill: url(#SVGID_1_)
}

.st7 {
  fill: #ff7300
}

.st8 {
  fill: #333
}

.st9 {
  fill: url(#SVGID_00000004513309622200152650000012548405442449557903_)
}

.st10 {
  fill: url(#SVGID_00000176009613972682108880000017653850478810576818_)
}

.st11 {
  fill: url(#SVGID_00000165231551871970503560000003355768082270395524_)
}

.st12 {
  fill: url(#SVGID_00000016765840912060849530000003864636654631869334_)
}

.st13 {
  fill: url(#SVGID_00000175318373803571573040000003520187963417351301_)
}

.st14 {
  fill: #00e9e9
}

.st15 {
  fill: #0025fc
}

.st16 {
  fill: url(#SVGID_00000176744785568834334890000016272680596910509955_)
}

.st17 {
  fill: url(#SVGID_00000142154143378922991730000015114578061633979577_)
}

.st18 {
  fill: url(#SVGID_00000057137345739774764380000005692279018787553672_)
}

.st19 {
  fill: url(#SVGID_00000018197760512707438830000005450277217363167631_)
}

.st20 {
  fill: #070102
}

.st21 {
  fill: #a12124
}

.st22 {
  fill: #d93a1b
}

.st23 {
  fill: #ce231b
}

.st24 {
  fill: #005bfe
}

.st25 {
  fill: #03adff
}

.st26 {
  fill: #0029c6
}

.st27 {
  fill: #19244d
}

.st28 {
  fill: url(#SVGID_00000099648853541386427350000011924859293268824251_)
}

.st29 {
  fill: url(#SVGID_00000151512695636114476310000004070120291506687652_)
}

.st30 {
  fill: #003f8d
}

.st31 {
  fill: url(#SVGID_00000000905668641983276990000015612520407539609484_)
}

.st32 {
  fill: url(#SVGID_00000008124702146073234230000017903708091573212054_)
}

.st33 {
  fill: url(#SVGID_00000053525533995258151490000016690075759732230830_)
}

.st34 {
  fill: url(#SVGID_00000149366792131534487140000007126687277309998505_)
}

.st35 {
  fill: #0345ff
}

.st36 {
  fill: #00a4ff
}

.st37 {
  fill: url(#SVGID_00000026162396317162936870000009323778367767395515_)
}

.st38 {
  fill: url(#SVGID_00000159453648451187762370000017662482324971942323_)
}

.st39 {
  fill: url(#SVGID_00000163066552177278987000000009955285394943042490_)
}

.st40 {
  fill: url(#SVGID_00000073680045162138477040000013841345853100508845_)
}

.st41 {
  opacity: .5
}

.st42 {
  fill: url(#SVGID_00000061440816753488309100000011002863956018966454_)
}

.st43 {
  fill: url(#SVGID_00000171689300392533340480000004446677872551114373_)
}

.st44 {
  fill: #285ef6
}

.st45 {
  fill: #00caff
}

.st46 {
  opacity: .5;
  fill: #333
}

.st47 {
  fill-rule: evenodd;
  clip-rule: evenodd;
  fill: #666
}

.st48 {
  fill: #f40
}

.st49 {
  fill-rule: evenodd;
  clip-rule: evenodd;
  fill: #333
}

.st50 {
  fill: #191966
}

.st51 {
  fill: #1049fe
}

.st52 {
  fill: #0930d1
}

.st53 {
  fill: #ff6e00
}

.st54 {
  fill: #b208f9
}

.st55 {
  fill: #00ff85
}

.st56 {
  fill: #00e7ff
}

.st57 {
  fill: url(#SVGID_00000085223301987141045820000015901426996138402463_)
}

.st58 {
  fill: url(#SVGID_00000117675828810432693600000014161767614204751503_)
}

.st59 {
  fill: url(#SVGID_00000065772599894272521050000003729229797858184842_)
}

.st60 {
  fill: url(#SVGID_00000108994254863165324150000004415376681824642177_)
}

.st61 {
  fill: url(#SVGID_00000036970705396437004980000000018245597956932796_)
}

.st62 {
  fill: url(#SVGID_00000103234607723036146170000014774975110779970213_)
}</style>
