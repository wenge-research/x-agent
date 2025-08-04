// 定义内容
export default {
	stepForm: {
		step: {
			title: 'Create Channel Forms',
			title2: {
				finish: 'Finish',
				channel: 'Channel Information',
				baseInfo: 'Select Basic Information'
			},
			subTitle: {
				baseInfo: 'Channel creation activitie',
				channel: 'Select upstream of domain',
				finish: 'Submit success',
			}
		},
		success:{
			title:"Success",
			subTitle:'The form is submitted successfully!',
		},
		button:{
			view: 'Detail',
			again: 'Again',
		},
		form:{
			description: {
				title: 'Channel Form Description',
				text: 'Advertiser channel promotion supports tracking of users who download apps by placing ads on third-party advertisers, such as toutiao channel, and tracking users who activate apps by downloading apps through channels.',
			},
			label: {
				activityName: 'Activity Name',
				channelType: 'Channel Type',
				promotionTime: 'Promotion Time',
				promoteLink: 'Promote Link',
				advertisingSource: 'Advertising Source',
				advertisingMedia: 'Advertising Media',
				keyword: 'keyword',
				pushNotify: 'Push Notify',
				advertisingContent: 'Advertising Content'
			},
			error: {
				activityName: {
					required: 'Please enter the activity name',
					pattern: 'Enter a maximum of 20 Chinese characters, letters, or digits',
				},
				channelType: {
					required:'Please select a channel type',
				},
				promotionTime: {
					required: 'Please select the promotion time'
				},
				promoteLink: {
					required: 'Please enter the promotion link',
					pattern: 'For example, the download address of Android or iOS or the intermediate URL must start with http:// or https://'
				},
				advertisingSource: {
					required: 'Please enter the advertising source'
				},
				advertisingMedia: {
					required: 'Please enter the advertising media'
				},
				keyword: {
					required: 'Please select keyword'
				},
				advertisingContent: {
					required: 'Please enter the description of advertisement content',
					maxLength: 'the maximum is 200 words'
				}
			},
			tip: {
				promoteLink: 'For example, the download address of Android or iOS or the intermediate URL must start with http:// or https://',
			}
		},
		placeholder: {
			activityName: 'Enter a maximum of 20 Chinese characters, letters, or digits',
			channelType: 'Select a channel type',
			promoteLink: 'Please enter the promotion page Link',
			advertisingSource: 'Introduction source address: Sohu, Sina',
			advertisingMedia: 'Marketing media: CPC, Banner, EDM',
			keyword: 'Please select keyword',
			advertisingContent: 'Please enter the description of advertisement content, the maximum is 200 words'

		}
	}
  
};
