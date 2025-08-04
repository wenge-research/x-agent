import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;

// 查询所有产业类型
export function queryIndustryType(){
  return request({
    url:baseUrl + "/lhmz/policyRewardInfo/queryIndustryType",
    method:"GET"
  })
}

interface IndustryParams {
  queryContent:string,
  regionLevel:string,
  industryType:string
}

// 查询产业数据
export function queryAll(params:IndustryParams){
  return request({
    url:baseUrl + "/lhmz/policyRewardInfo/queryAll",
    method:"GET",
    params
  })
}


// 投资线索收集
interface Invest {
  investorName:string,
  investmentIntentOverview:string,
  plannedTotalInvestment:number | string,
  investmentContactPerson:string,
  contactInformation:string
}

export function addInvestmentLead(data:Invest){
  return request({
    url:baseUrl + "/lhmz/addInvestmentLead",
    method:"POST",
    data
  })
}
// 投资线索收集lhmz/supermarketInfo/queryAll?latitude=&longitude=&queryContent=
interface InvestMap {
  latitude:number | string,
  longitude:number | string,
  queryContent:string,
  
}

export function queryAllMap(data:InvestMap){
  return request({
    url:baseUrl + "/lhmz/supermarketInfo/queryAll",
    method:"get",
    data
  })
}
// 投资线索收集lhmz/supermarketInfo/queryAll?latitude=&longitude=&queryContent=
interface InvestMapKj {
  latitude:number | string,
  longitude:number | string,
  purpose:string,
  
}

export function queryAllMapKj(data:InvestMapKj){
  return request({
    url:baseUrl + "/lhmz/industrialSpace2/queryAll?industryName="+data,
    method:"get"
  })
}
// 投资线索收集
interface Invest {
  name: string;
  content: string;
  contactInformation: string;
  companyName: string;
}

export function addLhmzMessage(data:Invest){
  return request({
    url:baseUrl + "/lhmz/addLhmzMessage",
    method:"POST",
    data
  })
}
