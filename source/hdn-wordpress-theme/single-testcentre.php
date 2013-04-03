<?php get_header(); ?>
		
		<div role="main" class="main">
			<div class="page_title">
				<div class="wrapper">
					<hgroup>
						<h1>Test Centre</h1>
						<h2>Find testing tools, data and guidance</h2>
					</hgroup>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>

				<div class="wrapper cf container">
					
					<div class="one_quarter left_bar">

						<?php if(get_field('file_download')): ?>
	 						<?php while(has_sub_field('file_download')): ?>
								<div class="download_item">
								 	<h4><?php the_sub_field('file_name'); ?></h4>
									<p><?php the_sub_field('file_size'); ?></p>
								 	<a href="<?php the_sub_field('file'); ?>" class="btn purple_btn dl_btn">Download Now <span class="btn_icon" aria-hidden="true" data-icon="&#x6c;"></span></a>
									<div class="clear"></div>
									<p class="sm_print"><?php the_sub_field('terms'); ?></p>
								</div>	
							<?php endwhile; ?> 
						<?php endif; ?>

						<?php if(get_field('file_download_link')): ?>
	 						<?php while(has_sub_field('file_download_link')): ?>
								<div class="download_item">
								 	<h4><?php the_sub_field('file_name'); ?></h4>
									<p><?php the_sub_field('file_size'); ?></p>
								 	<a href="<?php the_sub_field('file_link'); ?>" class="btn purple_btn dl_btn" target="_blank">Download Now <span class="btn_icon" aria-hidden="true" data-icon="&#x6c;"></span></a>
									<div class="clear"></div>
									<p class="sm_print"><?php the_sub_field('terms'); ?></p>
								</div>	
							<?php endwhile; ?> 
						<?php endif; ?>
					</div>
					
					<div class="three_quarters">
						<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>

							<div class="content_wrap cf">
								<article class="cf">
									<div class="three_quarters art_txt_area">
									<hgroup>
										<h2><?php the_title(); ?></h2>
										<h3 class="delta"><?php the_field('sub_title'); ?></h3>
									</hgroup>
									
									<div class="art_txt">
									<?php the_content(); ?>
									<?php include_once "likes.php"; ?>
									</div>
									</div><!--end three quarters-->

							<aside class="one_quarter link_lists">
								<?php if( get_field('link_list') ): ?>
								<?php while( has_sub_field('link_list') ): ?>
									<div class="list">
										<ul class="attachments <?php the_sub_field('ll_title'); ?>">
											<li>
												<h4><?php the_sub_field('ll_title'); ?></h4>
											</li>
											<?php if( get_sub_field('link') ): ?>
											<?php while( has_sub_field('link') ): ?>
												<li>
													<span class="att_icon" aria-hidden="true" data-icon="<?php the_sub_field('icon_font'); ?>"></span>
													<a href="<?php the_sub_field('link_url'); ?>" target="<?php the_sub_field('int_or_ext'); ?>"><?php the_sub_field('link_text'); ?></a>
												</li>
											<?php endwhile; ?>
											<?php endif; ?>
										</ul>
									</div><!--end list-->
								<?php endwhile; ?>
								<?php endif; ?>

								<?php include_once "share_tools.php"; ?>
							</aside><!--end one quarter-->

								</article>
							</div><!--end content wrap-->
						<?php endwhile; endif; ?>

					</div><!--end two thirds-->
					
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>
